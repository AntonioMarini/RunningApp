package com.apollyon.samproject.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apollyon.samproject.R
import com.apollyon.samproject.utilities.Converters
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

@Database(entities = [RunningSession::class, User::class, Achievement::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {


    abstract val runDao: RunDao
    abstract val userDao: UsersDao
    abstract val achievementDAo: AchievementsDao

    companion object {
        @Volatile
        private var INSTANCE: RunningDatabase? = null

        //job per coroutines
        private var job = Job()
        private val uiScope = CoroutineScope(Dispatchers.Main + job)

        fun getInstance(context: Context): RunningDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RunningDatabase::class.java,
                        "run_database"
                    )
                        .fallbackToDestructiveMigration() //migrare -> distrugge tutto
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                uiScope.launch {
                                    populateInitialData(context.applicationContext)
                                }
                            }
                        })
                        .build()
                    INSTANCE = instance

                }

                return instance
            }
        }


        private suspend fun populateInitialData(appcontext: Context) {
            withContext(IO) {
                getInstance(appcontext).achievementDAo.insertManyAchievements(
                    listOf(
                        Achievement(
                            null,
                            "Noob",
                            "Run for 100m",
                            scaleBitmaps(appcontext, R.drawable.material_01)
                        ),
                        Achievement(
                            null,
                            "Starter",
                            "Run for 1km",
                            scaleBitmaps(appcontext, R.drawable.material_02)
                        ),
                        Achievement(
                            null,
                            "Medium",
                            "Run for 5km",
                            scaleBitmaps(appcontext, R.drawable.material_03)
                        ),
                        Achievement(
                            null,
                            "Advanced",
                            "Run for 25km",
                            scaleBitmaps(appcontext, R.drawable.material_04)
                        ),
                        Achievement(
                            null,
                            "Marathon",
                            "Run for 50km",
                            scaleBitmaps(appcontext, R.drawable.material_05)
                        )
                    )
                )
            }
        }

        private fun scaleBitmaps(context: Context, id: Int): Bitmap? {
            // TODO logica per disabilitare filtro aliasing per pixel art
            return BitmapFactory.decodeResource(context.resources, id)
        }
    }
}









