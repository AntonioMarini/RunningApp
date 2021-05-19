package com.apollyon.samproject.datastruct

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RunningSession::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase(){
    
    abstract fun getRunningSessionDao() : RunningSessionsDao

}