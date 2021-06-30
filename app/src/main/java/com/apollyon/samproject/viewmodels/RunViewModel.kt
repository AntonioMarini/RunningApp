package com.apollyon.samproject.viewmodels

import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.data.RunDao

lateinit var runningSession : RunningSession

class RunViewModel(dataSource : RunDao?) : ViewModel() {

    // uso coroutines per inserire sessione nel database non nel run results fragment

    override fun onCleared() {
        super.onCleared()
    }
}