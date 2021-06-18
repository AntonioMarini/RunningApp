package com.apollyon.samproject.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.data.MissionsDao
import com.apollyon.samproject.data.RunDao
import com.apollyon.samproject.data.UsersDao
import java.lang.IllegalArgumentException

class MainViewModelFactory (private val usersDao: UsersDao,private val runDao: RunDao , private val missionsDao: MissionsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(usersDao, runDao, missionsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}