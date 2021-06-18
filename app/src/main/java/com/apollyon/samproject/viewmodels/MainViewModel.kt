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

class MainViewModel(private val usersDao: UsersDao, private val runDao: RunDao, private val missionsDao: MissionsDao) : ViewModel(){

    var authUser : FirebaseUser? = null
    private lateinit var userRealtimeReference : DatabaseReference

    //ottenuti dopo da room quando l'utente fa il login
    lateinit var user : LiveData<User>
    lateinit var allMissions : LiveData<List<Mission>> // per la recycler degli achievements
    lateinit var runSessions : LiveData<List<RunningSession>> // per la recyclerview nella home

    lateinit var totalKm : LiveData<Int>
    lateinit var totalTime : LiveData<Long>
    lateinit var totalAvgSpeed : LiveData<Float>
    lateinit var totalCalories : LiveData<Int>

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

    var pendingSnackbarInfo = MutableLiveData<String>("")

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
        userRealtimeReference = FirebaseDatabase.getInstance().getReference("users").child(uid)

        // prendo user da realtime database
        userRealtimeReference.get().addOnSuccessListener {
            _userFromRealtime.value = it.getValue<User>()
            insertNewUserLocal(_userFromRealtime.value!!)
        }

        userRealtimeReference.addValueEventListener(userListener)

        // inizializzo i campi che mi servono usando i dao
        user = usersDao.getUser(uid)
        allMissions = missionsDao.getAllMissions()
        runSessions = runDao.getAllRunsByDate(uid)

        totalKm = runDao.getTotalRunsDistance(authUser!!.uid)
        totalAvgSpeed = runDao.getTotalRunsAvgSpeed(uid)
        totalCalories = runDao.getRunsTotalCal(uid)
        totalTime = runDao.getTotalTimeInMillis(uid) // TODO da trasformare
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

    fun onRunFinished(totalkm : Int){
        val missions = allMissions.value
        if (missions != null) {
            for(m in missions){
                if(!m.complete!! && parseMissionCondition(m.condition!!, totalkm))
                    onMissionCompleted(m)
            }
        }
    }

    fun onMissionCompleted(m:Mission){
        m.complete = true
        updateMission(m)
        addXp(m.xpReward!!)
        pendingSnackbarInfo.postValue(m.name!! + " has been completed! Gained: ${m.xpReward}xp")
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

    /*-------------------------------------Missions Conditions-------------------------------------------------*/

    fun parseMissionCondition(condition: String, totalkm : Int) : Boolean{
        val tokens = condition.split(" ")
        return when(tokens[0]){
            "run" -> missionRunCondition(tokens.subList(1,tokens.size), totalkm)
            // altri eventuali tipi di condizione
            else -> false
        }
    }

    private fun missionRunCondition(tokens : List<String>, totalkm : Int) : Boolean{
        return when(tokens[0]){
            "distance" -> missionRunDistanceCondition(tokens.subList(1,tokens.size), totalkm)
            // altri eventuali parametri sulla condizione
            else -> false
        }
    }

    private fun missionRunDistanceCondition(tokens : List<String>, totalkm : Int) : Boolean{
        val operator = tokens[0]
        val value = tokens[1]

        return when(operator){
            ">" -> totalkm >=  value.toInt()
            //altri eventuali operatori
            else -> false
        }
    }

    //altre funzioni per altri tipi di missioni che hanno condizioni su utenti per esempio...

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

    // MISSIONS

    fun insertMission(mission: Mission){
        uiScope.launch {
            insertMissionDao(mission)
        }
    }

    fun insertMissions(missions: List<Mission>){
        uiScope.launch {
            insertMissionsDao(missions)
        }
    }

    fun updateMission(mission: Mission){
        uiScope.launch {
            updateMissionDao(mission)
        }
    }

    private suspend fun insertMissionDao(mission: Mission){
        withContext(IO){
            missionsDao.insertMission(mission)
        }
    }

    private suspend fun insertMissionsDao(missions: List<Mission>){
        withContext(IO){
            missionsDao.insertManyMissions(missions)
        }
    }

    private suspend fun updateMissionDao(mission: Mission){
        withContext(IO){
            missionsDao.updateMission(mission)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}