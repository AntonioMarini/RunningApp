package com.apollyon.samproject.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.R
import com.apollyon.samproject.auth.AuthViewModel
import com.apollyon.samproject.auth.AuthViewModelFactory
import com.apollyon.samproject.data.RunningDatabase

class AuthActivity : AppCompatActivity(){


    private lateinit var authViewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource = RunningDatabase.getInstance(application).userDao
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(dataSource)).get(AuthViewModel::class.java)

        setContentView(R.layout.activity_auth)
    }

}