package com.apollyon.samproject

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.datastruct.FirebaseSupport
import com.apollyon.samproject.datastruct.RunningSessionsDao
import com.apollyon.samproject.datastruct.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream

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



}