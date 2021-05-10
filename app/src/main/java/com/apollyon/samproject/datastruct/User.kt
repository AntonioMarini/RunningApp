package com.apollyon.samproject.datastruct

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val uid : String? = null, val email: String? = null, val username: String? = null, val age: Int? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}