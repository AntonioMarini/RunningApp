package com.apollyon.samproject.datastruct

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunningSessionsDao {

    @Insert
    suspend fun insert(session: RunningSession)

    @Update
    suspend fun update(session: RunningSession)

    @Delete
    suspend fun delete(session: RunningSession)

    @Query("select * from running_sessions where sessionID = :sessionID and user_id = :uid")
    fun getSession(sessionID: Long, uid: String?) : RunningSession

    @Query("select * from running_sessions  where user_id = :uid order by timestamp desc")
    fun getAllRunsByDate(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user_id = :uid order by avg_speed desc")
    fun getAllRunsByAvgSpeed(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user_id = :uid order by calories_burned desc")
    fun getAllRunsByCalBurned(uid: String?) : LiveData<List<RunningSession>>

    @Query("select * from running_sessions where user_id = :uid order by distance_meters desc")
    fun getAllRunsByDistance(uid: String?) : LiveData<List<RunningSession>>

    @Query("select sum(time_milli) from running_sessions where user_id = :uid")
    fun getTotalTimeInMillis(uid: String?): LiveData<Long>

    @Query("select sum(calories_burned) from running_sessions where user_id = :uid")
    fun getTotalCal(uid: String?): LiveData<Int>

    @Query("select sum(distance_meters) from running_sessions where user_id = :uid")
    fun getTotalDistance(uid: String?): LiveData<Int>

    @Query("select avg(avg_speed) from running_sessions where user_id = :uid")
    fun getTotalAvgSpeed(uid: String?): LiveData<Float>

}