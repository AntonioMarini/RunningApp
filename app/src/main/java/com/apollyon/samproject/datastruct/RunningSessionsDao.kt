package com.apollyon.samproject.datastruct

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunningSessionsDao {

    @Insert
    fun insert(session: RunningSession)

    @Update
    fun update(session: RunningSession)

    @Delete
    fun delete(session: RunningSession)

    @Query("select * from running_sessions where sessionID = :sessionID and user_id = :uid")
    fun getSession(sessionID: Long, uid: String) : RunningSession

    @Query("select * from running_sessions where user_id = :uid order by start_time desc")
    fun getAllSessions(uid: String) : LiveData<List<RunningSession>>

}