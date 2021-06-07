package com.apollyon.samproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.apollyon.samproject.utilities.Converters

@Database(entities = [RunningSession::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase(){

    abstract val runDao : RunDao
    abstract val userDao : UsersDao

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
                        "run_database"
                    )
                        .fallbackToDestructiveMigration() //migrare -> distrugge tutto
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }



}