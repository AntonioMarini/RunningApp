package com.apollyon.samproject.auth

import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.User
import com.apollyon.samproject.data.UsersDao
import kotlinx.coroutines.*

class AuthViewModel(private val dataSource: UsersDao) : ViewModel() {

    //job per coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Room

    fun insertNewUserLocal( user: User){
        uiScope.launch {
            insertUser(user)
        }
    }

    private suspend fun insertUser(user: User){
        withContext(Dispatchers.IO){
            dataSource.insert(user)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}