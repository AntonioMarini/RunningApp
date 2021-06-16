package com.apollyon.samproject.viewmodels

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.*
import com.apollyon.samproject.utilities.LevelUtil
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainViewModel(private val usersDao: UsersDao, private val runDao: RunDao, private val achievementsDao: AchievementsDao) : ViewModel(){

    var authUser : FirebaseUser? = null
    private lateinit var userRealtimeReference : DatabaseReference

    //ottenuti dopo da room quando l'utente fa il login
    lateinit var user : LiveData<User>
    lateinit var allAchievements : LiveData<List<Achievement>> // per la recycler degli achievements
    lateinit var runSessions : LiveData<List<RunningSession>> // per la recyclerview nella home
    lateinit var totalKm : LiveData<Int>  //total km

    //to hide/show the topbar and navbar
    private val _shouldHideBars = MutableLiveData<Boolean>(false)
    val shouldHideBars :LiveData<Boolean> get() = _shouldHideBars

    //per fare download/upload dell immagine del profilo
    private val _profileImageDownloaded = MutableLiveData<Uri>()
    val profileImageDownloaded : LiveData<Uri> get() = _profileImageDownloaded
    private val storageReference = FirebaseStorage.getInstance().reference

    //job per coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _userFromRealtime = MutableLiveData<User?>()
    val userFromRealtime : LiveData<User?> get() = _userFromRealtime

    // listener che ascolta gli update all'utente da firebase
    private val userListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI and the room database
            dataSnapshot.getValue<User>()?.let { updateUserLocal(it) }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    fun uploadImage(uri: Uri){
        val profileImageRef: StorageReference = storageReference.child("images/" + user.value?.uid + "/profile/profile.jpg")

        val uploadTask = profileImageRef.putFile(uri)
        uploadTask.addOnFailureListener {
            //TODO error unsuccessful upload
            Log.i("UPLOAD", "unsuccesful uploading")

        }.addOnSuccessListener { taskSnapshot ->
            Log.i("UPLOAD", "Image has been uploaded")
            _profileImageDownloaded.value = uri
            getDownloadUrl(profileImageRef)
        }
    }

    fun onUserLogged(uid:String){
        authUser = Firebase.auth.currentUser

        userRealtimeReference = FirebaseDatabase.getInstance().getReference("users").child(authUser!!.uid)

        // prendo user da realtime database
        userRealtimeReference.get().addOnSuccessListener {
            _userFromRealtime.value = it.getValue<User>()
            insertNewUserLocal(_userFromRealtime.value!!)
        }

        userRealtimeReference.addValueEventListener(userListener)

        // inizializzo i campi che mi servono usando i dao
        user = usersDao.getUser(authUser!!.uid)
        allAchievements = achievementsDao.getAllAchievements()
        runSessions = runDao.getAllRunsByDate(authUser?.uid)
        totalKm = runDao.getTotalRunsDistance(authUser?.uid)
    }

    // update immgine profilo
    private fun getDownloadUrl(reference : StorageReference){
        reference.downloadUrl.addOnSuccessListener {uri ->
            Log.i("DOWNLOAD", "download successful")
            updateProfileImage(uri)
        }
    }
    private fun updateProfileImage(uri : Uri){
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        authUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User profile updated.")
            }
        }
    }

    fun addXp(xpGained: Long){

        var initialXpToNextLevel = _userFromRealtime.value!!.xpToNextLevel!!
        var xpRemaining = initialXpToNextLevel - xpGained; // puo essere negativo

        while(xpRemaining<0){
            levelUp()
            initialXpToNextLevel = _userFromRealtime.value!!.xpToNextLevel!!
            xpRemaining += initialXpToNextLevel
        }

        _userFromRealtime.value!!.xpToNextLevel =  xpRemaining;
        userRealtimeReference.child("xpToNextLevel").setValue(_userFromRealtime.value!!.xpToNextLevel)
        userRealtimeReference.child("level").setValue(_userFromRealtime.value!!.level)
    }

    private fun levelUp(){
        if(_userFromRealtime.value!!.level != null) {
            _userFromRealtime.value!!.level = _userFromRealtime.value!!.level!! + 1
            val newLevel = _userFromRealtime.value!!.level!!
            _userFromRealtime.value!!.xpToNextLevel = LevelUtil.xpForNextLevel(newLevel)
        }
    }

    /**
     * Called when the run fragment is opened
     * its used to hide the useless ui of the main activity
     */
    fun onHideBars(){
        _shouldHideBars.value = true
    }

    fun onShowBars(){
        _shouldHideBars.value = false
    }

    /*-------------------------------------ROOM-------------------------------------------------*/
    // TODO metti tuttp in un repository

    // comunicazione col database -> uso coroutines

    //SESSIONS

    fun clearSessions(){
        uiScope.launch {
            clearAll(authUser!!.uid)
        }
    }

    fun insertSession(session : RunningSession){
        uiScope.launch {
            insertSessionDao(session)
        }
    }

    /**
     * insert session in the database
     * @param session session to add
     */
    private suspend  fun insertSessionDao(session: RunningSession){
        // insert missing data in the session like the user id
        session.user = authUser!!.uid

        withContext(IO){
            runDao.insertRun(session)
        }
    }

    private suspend fun clearAll(uid : String?){
        withContext(IO){
            runDao.deleteAllRunsOfUser(uid)
        }
    }

    //USERS

    fun insertNewUserLocal( user: User){
        uiScope.launch {
            insertUserDao(user)
        }
    }

    private suspend fun insertUserDao(user: User){
        withContext(IO){
            usersDao.insert(user)
        }
    }

    fun updateUserLocal( user: User){
        uiScope.launch {
            updateUserDao(user)
        }
    }

    private suspend fun updateUserDao(user: User){
        withContext(IO){
            usersDao.update(user)
        }
    }

    // ACHIEVEMENTS

    fun insertAchievements(achievements: List<Achievement>){
        uiScope.launch {
            insertAchievementsDao(achievements)
        }
    }

    fun insertAchievement(achievement: Achievement){
        uiScope.launch {
            insertAchievementDao(achievement)
        }
    }

    private suspend fun insertAchievementDao(achievement: Achievement){
        withContext(IO){
            val idInserted = achievementsDao.insertAchievement(achievement)
        }
    }

    private suspend fun insertAchievementsDao(achievements: List<Achievement>){
        withContext(IO){
            val achievementsIds = achievementsDao.insertManyAchievements(achievements)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}