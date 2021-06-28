package com.apollyon.samproject.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningDatabase
import com.apollyon.samproject.utilities.LevelUtil
import com.apollyon.samproject.viewmodels.MainViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import com.gu.toolargetool.TooLargeTool;


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TooLargeTool.startLogging(application);
        //viewmodel principale con cui tutti i fragment comunicano, mantiene il repository con tutti i dao
        viewModel = ViewModelProvider(this, MainViewModelFactory(application)).get(MainViewModel::class.java)

        // bottom navigation
        val navHostFragment : NavHostFragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment?
        if (navHostFragment != null) NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
        val levelText = findViewById<TextView>(R.id.level_text)
        val progressBar = findViewById<ProgressBar>(R.id.progress_top)

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
                    navHostFragment.navController.graph.startDestination = R.id.home
                }

                val contextOfSnackbar = findViewById<ConstraintLayout>(R.id.main_constraint)
                viewModel.pendingSnackbarInfo.observe(this, Observer {
                    if(it != ""){
                        Snackbar.make(contextOfSnackbar, it, Snackbar.LENGTH_SHORT).show()
                        viewModel.pendingSnackbarInfo.postValue("")
                    }
                })

                if(viewModel.authUser?.photoUrl != null)
                    changePicture(viewModel.authUser?.photoUrl)
                viewModel.profileImageDownloaded.observe(this, Observer {uri ->
                    changePicture(uri)
                })

                viewModel.userFromRealtime.observe(this, Observer {
                    if (it != null) {
                        levelText.text = "Lvl ${it.level}"
                        progressBar.max = LevelUtil.xpForNextLevel(it.level!! - 1).toInt()
                        progressBar.progress = progressBar.max - it.xpToNextLevel!!.toInt()
                    }
                })
            }
        })
    }

    private fun changePicture(uri : Uri?){
        val imageView = findViewById<ImageView>(R.id.profile_image)
        Glide.with(this)
            .load(uri).into(imageView)
    }
}