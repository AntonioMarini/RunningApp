package com.apollyon.samproject.data

import androidx.room.*

@Dao
interface UsersDao {

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

}