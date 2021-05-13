package com.apollyon.samproject.newsession

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.datastruct.RunningSessionsDao
import java.lang.IllegalArgumentException

class SessionViewModelFactory (
    private val dataSource: RunningSessionsDao,
    private val application: Application) : ViewModelProvider.Factory {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SessionViewModel::class.java)){
                return SessionViewModel(dataSource, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}