package com.apollyon.samproject.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Entity relativa a un utente, viene salvato sia su firebase che in locale usando room e dao
 */
@Entity(
    tableName = "users"
)
data class User(

    @NonNull
    @PrimaryKey(autoGenerate = false)
    var uid : String = "", // non si autogenera, la sceglie gia firebase al momento della registrazione

    @ColumnInfo(name = "email")
    var email: String? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "age")
    var age: Int? = null,

    @ColumnInfo(name = "height")
    var height: Float? = null,

    @ColumnInfo(name = "weight")
    var weight: Float? = null,

    @ColumnInfo(name = "level")
    var level: Int? = null,

    @ColumnInfo(name = "xpToNextLevel")
    var xpToNextLevel: Long? = null

)