package com.apollyon.samproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("select * from users where uid = :uid")
    fun getUser(uid: String) : LiveData<User>

}