package com.apollyon.samproject.data

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * class that represents an entity of run for the room database component
 *
 */
@Parcelize
@Entity(
        tableName = "running_sessions",
        foreignKeys = [ForeignKey(
                entity = User::class,
                parentColumns = arrayOf("uid"),
                childColumns = arrayOf("user"),
                onDelete = ForeignKey.CASCADE
        )]
)
data class RunningSession(

        @PrimaryKey(autoGenerate = true)
        val session_id : Long? = null,

        @ColumnInfo(name = "user",index = true)
        var user : String? = null,

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
