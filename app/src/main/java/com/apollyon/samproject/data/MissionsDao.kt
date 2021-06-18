package com.apollyon.samproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MissionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(Mission: Mission) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyMissions(Missions: List<Mission>) : Array<Long>

    @Delete
    suspend fun deleteMission(Mission: Mission)

    @Update
    suspend fun updateMission(Mission: Mission)

    @Transaction
    @Query("select * from missions")
    fun getAllMissions(): LiveData<List<Mission>>

}