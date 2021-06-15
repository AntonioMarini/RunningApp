package com.apollyon.samproject.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.data.AchievementsDao
import com.apollyon.samproject.data.RunDao
import com.apollyon.samproject.data.UsersDao
import java.lang.IllegalArgumentException

class MainViewModelFactory (private val usersDao: UsersDao,private val runDao: RunDao , private val achievementsDao: AchievementsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(usersDao, runDao, achievementsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}