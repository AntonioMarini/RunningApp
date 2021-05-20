package com.apollyon.samproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.databinding.ActivityRunBinding
import com.apollyon.samproject.datastruct.RunningDatabase
import com.apollyon.samproject.datastruct.RunningSession
import com.apollyon.samproject.datastruct.RunningSessionsDao
import com.apollyon.samproject.run.RunViewModel
import com.apollyon.samproject.run.RunViewModelFactory


class RunActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRunBinding

    private lateinit var viewModel: RunViewModel

    private lateinit var dataSource: RunningSessionsDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataSource = RunningDatabase.getInstance(this).runningSessionDao

        viewModel = ViewModelProvider(this, RunViewModelFactory(dataSource)).get(RunViewModel::class.java)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}