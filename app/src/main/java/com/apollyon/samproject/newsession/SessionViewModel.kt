package com.apollyon.samproject.newsession

import android.app.Application
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.datastruct.RunningSessionsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SessionViewModel(val database: RunningSessionsDao,
                    application: Application
) : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}