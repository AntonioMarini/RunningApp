package com.apollyon.samproject.datastruct

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "running_sessions")
data class RunningSession(

        @PrimaryKey(autoGenerate = true)
        val sessionID : Long = 0L,

        @ColumnInfo(name = "user_id")
        val uid : String,

        @ColumnInfo(name = "start_time")
        var startTimeMilli : Long,

        @ColumnInfo(name = "end_time")
        var endTimeMilli : Long,

        @ColumnInfo(name = "float")
        var kilometers : Float

) : Parcelable
