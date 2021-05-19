package com.apollyon.samproject.datastruct

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "running_sessions")
data class RunningSession(

        @PrimaryKey(autoGenerate = true)
        val sessionID : Long? = null,

        @ColumnInfo(name = "user_id")
        val uid : String? = null,

        @ColumnInfo(name = "map_screen")
        var map_screen: Bitmap? = null,

        @ColumnInfo(name = "avg_speed")
        var avgSpeedInKMH: Float = 0f,

        @ColumnInfo(name = "calories_burned")
        var caloriesBurned: Int = 0,

        @ColumnInfo(name = "distance_meters")
        var distanceInMeters: Int = 0,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long = 0L,

        @ColumnInfo(name = "time_milli")
        var timeMilli : Long = 0L,

        ) : Parcelable{
}
