package com.apollyon.samproject.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "achievements")
data class Achievement (
    @PrimaryKey(autoGenerate = true)
    var achievementId: Long? = null,

    var name: String? = null,

    var description: String? = null,

    var iconBitmap: Bitmap? = null

){

}
