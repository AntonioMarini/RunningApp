package com.apollyon.samproject.datastruct

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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

)
