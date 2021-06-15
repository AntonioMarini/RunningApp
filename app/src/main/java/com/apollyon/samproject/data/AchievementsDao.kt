package com.apollyon.samproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AchievementsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyAchievements(achievements: List<Achievement>) : Array<Long>

    @Delete
    suspend fun deleteAchievement(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)


    @Query("select * from achievements")
    fun getAllAchievements(): LiveData<List<Achievement>>

}