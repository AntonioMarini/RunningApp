package com.apollyon.samproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    @Insert
    suspend fun insertRun(session: RunningSession)

    @Update
    suspend fun updateRun(session: RunningSession)

    @Delete
    suspend fun deleteRun(session: RunningSession)

    @Query("delete from running_sessions where user = :uid")
    suspend fun deleteAllRunsOfUser(uid: String?)

    @Query("select * from running_sessions where session_id = :sessionID and user = :uid")
    fun getRun(sessionID: Long, uid: String?) : RunningSession

    @Query("select * from running_sessions order by timestamp desc")
    fun getAllRuns() : LiveData<List<RunningSession>>

    @Query("select * from running_sessions  where user = :uid order by timestamp desc")
    fun getAllRunsByDate(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user = :uid order by avg_speed desc")
    fun getAllRunsByAvgSpeed(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user = :uid order by calories_burned desc")
    fun getAllRunsByCalBurned(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user = :uid order by distance_meters desc")
    fun getAllRunsByDistance(uid: String?) : LiveData<List<RunningSession>>

    @Query("select sum(time_milli) from running_sessions where user = :uid")
    fun getTotalTimeInMillis(uid: String?): LiveData<Long>

    @Query("select sum(calories_burned) from running_sessions where user = :uid")
    fun getRunsTotalCal(uid: String?): LiveData<Int>

    @Transaction
    @Query("select sum(distance_meters) from running_sessions where user = :uid")
    fun getTotalRunsDistance(uid: String?): LiveData<Int>

    @Query("select avg(avg_speed) from running_sessions where user = :uid")
    fun getTotalRunsAvgSpeed(uid: String?): LiveData<Float>

}