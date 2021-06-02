package com.apollyon.samproject.viewmodels

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.utilities.FirebaseSupport
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.data.RunningSessionsDao
import com.apollyon.samproject.data.User
import com.apollyon.samproject.utilities.RunUtil
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainViewModel(public val database: RunningSessionsDao?) : ViewModel(){

    private val firebaseSupport : FirebaseSupport = FirebaseSupport()

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user

    private val _profileImageDownloaded = MutableLiveData<Uri>()
    val profileImageDownloaded : LiveData<Uri> get() = _profileImageDownloaded

    private val _userReference = MutableLiveData<DatabaseReference>()
    val userReference : LiveData<DatabaseReference> get() = _userReference

    private val storageReference = FirebaseStorage.getInstance().reference
    val authUser: FirebaseUser? = firebaseSupport.currentUser

    // per la recyclerview
    val runSessions = database!!.getAllRunsByDate(authUser?.uid)

    //total km
    val totalkm = database!!.getTotalDistance(authUser?.uid)

    //to hide/show the topbar and navbar
    private val _shouldHideBars = MutableLiveData<Boolean>(false)
    val shouldHideBars :LiveData<Boolean> get() = _shouldHideBars

    //job per coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    init {
        if (authUser != null) {
            _userReference.value = FirebaseDatabase.getInstance().getReference("users").child(authUser.uid)
            _userReference.value!!.addValueEventListener(userListener)
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
            firebaseSupport.updateProfileImage(uri)
        }
    }

    /**
     * Called when the run fragment is opened
     * its used to hide the useless ui of the main activity
     */
    fun onRunStarted(){
        _shouldHideBars.value = true
    }

    fun onRunFinished(){
        _shouldHideBars.value = false
    }

    // comunicazione col database -> uso coroutines

    fun clearSessions(){
        uiScope.launch {
            clearAll(_user.value?.uid)
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
        session.uid = _user.value?.uid

        withContext(IO){
            database?.insert(session)
        }
    }

    private suspend fun clearAll(uid : String?){
        withContext(IO){
            database?.deleteAll(uid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}