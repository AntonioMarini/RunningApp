package com.apollyon.samproject.run

import androidx.lifecycle.ViewModel
import com.apollyon.samproject.datastruct.RunningSession
import com.apollyon.samproject.datastruct.RunningSessionsDao

lateinit var runningSession : RunningSession

class RunViewModel(dataSource : RunningSessionsDao?) : ViewModel() {

    // uso coroutines per inserire sessione nel database non nel run results fragment


}