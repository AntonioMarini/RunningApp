package com.apollyon.samproject.viewmodels

import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.data.RunningSessionsDao

lateinit var runningSession : RunningSession

class RunViewModel(dataSource : RunningSessionsDao?) : ViewModel() {

    // uso coroutines per inserire sessione nel database non nel run results fragment

    override fun onCleared() {
        super.onCleared()
    }


}