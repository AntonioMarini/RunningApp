package com.apollyon.samproject.utilities

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// support class for handling firebase authentication, realtime database, storage
class FirebaseSupport() {

    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var userDatabaseReference : DatabaseReference
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    val currentUser : FirebaseUser? = auth.currentUser

    init {
        if (currentUser != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
        }
    }

    fun updateProfileImage(uri : Uri){
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }

        currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User profile updated.")
            }
        }
    }


}