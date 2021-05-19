package com.apollyon.samproject

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.apollyon.samproject.datastruct.RunningDatabase
import com.apollyon.samproject.home.HomeFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val dataSource = RunningDatabase.getInstance(application).runningSessionDao

        viewModel = ViewModelProvider(this , MainViewModelFactory(null)).get(MainViewModel::class.java)

        val navHostFragment : NavHostFragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment?
        if (navHostFragment != null) NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)

        if(viewModel.authUser?.photoUrl != null)
            changePicture(viewModel.authUser?.photoUrl)

        viewModel.profileImageDownloaded.observe(this, Observer {uri ->
            changePicture(uri)
        })

    }


    private fun changePicture(uri : Uri?){
        val imageView = findViewById<ImageView>(R.id.profile_image)
        Glide.with(this)
            .load(uri).into(imageView)
    }

}