package com.apollyon.samproject

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.datastruct.RunningSessionsDao
import com.apollyon.samproject.newsession.SessionViewModel
import java.lang.IllegalArgumentException

class MainViewModelFactory (private val dataSource: RunningSessionsDao? ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}