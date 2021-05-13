package com.apollyon.samproject.run

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apollyon.samproject.databinding.ActivityRunBinding
import com.apollyon.samproject.datastruct.RunningSession


class RunActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRunBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.stopButt.setOnClickListener { returnSession() }
    }


    private fun returnSession(){
        val data : Intent = Intent()

//---set the data to pass back---
        data.putExtra("session", RunningSession(0,"aaa", System.currentTimeMillis(),System.currentTimeMillis(), 0f));
        setResult(RESULT_OK, data);
//---close the activity---
        finish();
    }

}