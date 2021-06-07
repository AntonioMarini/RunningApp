package com.apollyon.samproject.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

/**
 * Entity relativa a un utente, viene salvato sia su firebase che in locale sullo smartphone
 */
@Entity(
    tableName = "users"
)
data class User(

    @NonNull
    @PrimaryKey(autoGenerate = false)
    var uid : String = "", // non si autogenera, la sceglie gia firebase

    @ColumnInfo(name = "email")
    var email: String? = null,

    @ColumnInfo(name = "username")
    var username: String? = null,

    @ColumnInfo(name = "age")
    var age: Int? = null,

    @ColumnInfo(name = "height")
    var height: Float? = null,

    @ColumnInfo(name = "weight")
    var weight: Float? = null

)