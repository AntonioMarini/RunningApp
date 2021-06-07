package com.apollyon.samproject.viewmodels

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.RunDao
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.data.User
import com.apollyon.samproject.data.UsersDao
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

class MainViewModel(private val usersDao: UsersDao, private val runDao: RunDao?) : ViewModel(){

    private val firebaseUser = Firebase.auth.currentUser

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user

    private val _profileImageDownloaded = MutableLiveData<Uri>()
    val profileImageDownloaded : LiveData<Uri> get() = _profileImageDownloaded

    //per fare download/upload dell immagine del profilo
    private val storageReference = FirebaseStorage.getInstance().reference

    val authUser : FirebaseUser? = Firebase.auth.currentUser

    // per la recyclerview
    val runSessions = runDao!!.getAllRunsByDate(authUser?.uid)

    //total km
    val totalkm = runDao!!.getTotalRunsDistance(authUser?.uid)

    //to hide/show the topbar and navbar
    private val _shouldHideBars = MutableLiveData<Boolean>(false)
    val shouldHideBars :LiveData<Boolean> get() = _shouldHideBars

    //job per coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // listener che ascolta gli update all'utente da firebase
    private val userListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            _user.value = dataSnapshot.getValue<User>()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    fun uploadImage(uri: Uri){
        val profileImageRef: StorageReference = storageReference.child("images/" + _user.value?.uid + "/profile/profile.jpg")

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

        authUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User profile updated.")
            }
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

    // comunicazione col database -> uso coroutines

    fun clearSessions(){
        uiScope.launch {
            clearAll(authUser!!.uid)
        }
    }

    fun insertSession(session : RunningSession){
        uiScope.launch {
            insert(session)
        }
    }

    /**
     * insert session in the database
     * @param session session to add
     */
    private suspend  fun insert(session: RunningSession){

        // insert missing data in the session like the user id
        session.user = authUser!!.uid

        withContext(IO){
            runDao?.insertRun(session)
        }
    }

    private suspend fun clearAll(uid : String?){
        withContext(IO){
            runDao?.deleteAllRunsOfUser(uid)
        }
    }

    fun insertNewUserLocal( user: User){
        uiScope.launch {
            insertUser(user)
        }
    }

    private suspend fun insertUser(user: User){
        withContext(Dispatchers.IO){
            usersDao.insert(user)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}