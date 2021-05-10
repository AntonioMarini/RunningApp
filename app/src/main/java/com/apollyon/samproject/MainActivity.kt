package com.apollyon.samproject

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.iterator
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.databinding.ActivityMainBinding
import com.apollyon.samproject.home.HomeFragment
import com.apollyon.samproject.newsession.NewSessionFragment
import com.apollyon.samproject.profile.ProfileFragment
import com.apollyon.samproject.stats.StatsFragment
import com.apollyon.samproject.trainer.TrainerFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val profileImage : CircleImageView = binding.profileImage

        if(viewModel.authUser?.photoUrl != null){
            Glide.with(this)
                    .load(viewModel.authUser?.photoUrl).into(profileImage)
        }else{
            Log.d(ContentValues.TAG,"userUri is null")
        }

        viewModel.profileImageDownloaded.observe(this, Observer {uri ->
            Glide.with(this /* context */)
                .load(uri)
                .into(profileImage)
        })

        val fragment: HomeFragment

        if (savedInstanceState == null) {
            fragment = HomeFragment.newInstance()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment, "home")
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit()
        }

        //setSupportActionBar(toolbar)

        val bottomNavigationView : BottomNavigationView= binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    val fragment = HomeFragment.newInstance()
                    replaceFragment(fragment, "home")
                    for (it in bottomNavigationView.menu){
                        it.isEnabled = true
                    }
                    item.isEnabled = false
                    true
                }
                R.id.stats -> {
                    // Respond to navigation item 2 click

                    val fragment = StatsFragment.newInstance("","")
                    replaceFragment(fragment, "stats")
                    for (it in bottomNavigationView.menu){
                        it.isEnabled = true
                    }
                    item.isEnabled = false
                    true
                }
                R.id.new_session -> {
                    // Respond to navigation item 2 click
                    val fragment = NewSessionFragment.newInstance("","")
                    replaceFragment(fragment,"new")
                    for (it in bottomNavigationView.menu){
                        it.isEnabled = true
                    }
                    item.isEnabled = false
                    true
                }
                R.id.trainer -> {
                    // Respond to navigation item 2 click

                    val fragment = TrainerFragment.newInstance("","")
                    replaceFragment(fragment,"train")
                    for (it in bottomNavigationView.menu){
                        it.isEnabled = true
                    }
                    item.isEnabled = false
                    true
                }
                R.id.profile -> {
                    // Respond to navigation item 2 click

                    val fragment = ProfileFragment.newInstance()
                    replaceFragment(fragment,"profile")
                    for (it in bottomNavigationView.menu){
                        it.isEnabled = true
                    }
                    item.isEnabled = false
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment:Fragment, tag:String){

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment, tag)
        //fragmentTransaction.addToBackStack(null)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit()
    }

}