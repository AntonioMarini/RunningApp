package com.apollyon.samproject.ui.activities

import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.viewmodels.MainViewModelFactory
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningDatabase
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var isOflline = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataSource = RunningDatabase.getInstance(application)



        //viewmodel principale con cui tutti i fragment comunicano, mantiene il dao
        viewModel = ViewModelProvider(this , MainViewModelFactory(dataSource.userDao, dataSource.runDao, dataSource.achievementDAo)).get(MainViewModel::class.java)

        // bottom navigation
        val navHostFragment : NavHostFragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment?
        if (navHostFragment != null) NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)

        if(viewModel.authUser?.photoUrl != null)
            changePicture(viewModel.authUser?.photoUrl)

        viewModel.profileImageDownloaded.observe(this, Observer {uri ->
            changePicture(uri)
        })

        val toolbar : androidx.appcompat.widget.Toolbar= findViewById(R.id.toolbar_cont)
        //setSupportActionBar(toolbar)


        viewModel.shouldHideBars.observe(this, Observer{ shouldHide ->
            if(shouldHide) {
                bottomNavigationView.visibility = GONE
                toolbar_cont.visibility = GONE

                if (navHostFragment != null) {
                    navHostFragment.navController.graph.startDestination = R.id.loginFragment
                }

            }
            else {
                bottomNavigationView.visibility = VISIBLE
                toolbar_cont.visibility = VISIBLE

                if (navHostFragment != null) {
                    navHostFragment.navController.graph.startDestination = R.id.achievements
                }
            }
        })

    }

    private fun changePicture(uri : Uri?){
        val imageView = findViewById<ImageView>(R.id.profile_image)
        Glide.with(this)
            .load(uri).into(imageView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val navController = findNavController(R.id.nav_host_fragment_container)
        return super.onOptionsItemSelected(item)
    }

}