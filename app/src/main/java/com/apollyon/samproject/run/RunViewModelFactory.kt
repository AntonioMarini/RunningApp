package com.apollyon.samproject.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.datastruct.RunningSessionsDao
import java.lang.IllegalArgumentException

class RunViewModelFactory(private val dataSource: RunningSessionsDao? ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunViewModel::class.java)){
            return RunViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}