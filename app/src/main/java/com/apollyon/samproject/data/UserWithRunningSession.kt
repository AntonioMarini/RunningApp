package com.apollyon.samproject.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * entity per modellare la relazione uno a molti tra User e RunnongSession
 */
data class UserWithRunningSession (
    @Embedded val user: User,

    @Relation(
        parentColumn = "uid",
        entityColumn = "user"
    )
    val runningSessions : List<RunningSession>
    )