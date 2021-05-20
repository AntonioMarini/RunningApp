package com.apollyon.samproject.datastruct

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RunningSession::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase(){

    abstract val runningSessionDao : RunningSessionsDao

    companion object{
        @Volatile
        private var INSTANCE: RunningDatabase? = null

        fun getInstance(context : Context) : RunningDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RunningDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}