package com.apollyon.samproject.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "missions")
data class Mission (
    @PrimaryKey(autoGenerate = true)
    var missionId: Long? = null,

    var name: String? = null,

    var description: String? = null,

    var iconBitmap: Bitmap? = null,

    var condition :String? = null,

    var xpReward : Long? = null,

    var complete : Boolean? = null
){

}
