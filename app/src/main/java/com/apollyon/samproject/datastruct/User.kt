package com.apollyon.samproject.datastruct

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(

    val uid : String? = null,

    val email: String? = null, val

    username: String? = null, val

    age: Int? = null

)