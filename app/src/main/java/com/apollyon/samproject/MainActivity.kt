package com.apollyon.samproject

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.databinding.ActivityMainBinding
import com.apollyon.samproject.datastruct.RunningDatabase
import com.apollyon.samproject.home.HomeFragment
import com.apollyon.samproject.newsession.NewSessionFragment
import com.apollyon.samproject.profile.ProfileFragment
import com.apollyon.samproject.stats.StatsFragment
import com.apollyon.samproject.trainer.TrainerFragment
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataSource = RunningDatabase.getInstance(application).runningSessionDao
        viewModel = ViewModelProvider(this, MainViewModelFactory(dataSource)).get(MainViewModel::class.java)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this

        if(viewModel.authUser?.photoUrl != null)
            changePicture(viewModel.authUser?.photoUrl)

        viewModel.profileImageDownloaded.observe(this, Observer {uri ->
            changePicture(uri)
        })

        // imposto HomeFragment come fragment iniziale
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), "home")
        }

        // keep this to enable it again when changing fragments
        var previousItemSelected : MenuItem = binding.bottomNavigation.menu.findItem(R.id.home)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {  item ->

            previousItemSelected.isEnabled = true
            item.isEnabled = false
            previousItemSelected = item

            when(item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment(), "home")
                    true
                }
                R.id.stats -> {
                    replaceFragment(StatsFragment(), "stats")
                    true
                }
                R.id.new_session -> {
                    replaceFragment(NewSessionFragment(),"new")
                    true
                }
                R.id.trainer -> {
                    replaceFragment(TrainerFragment(),"train")
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment(),"profile")
                    true
                }

                else -> false
            }

        }
    }

    private fun replaceFragment(fragment:Fragment, tag:String){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment, tag)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit()
    }

    private fun changePicture(uri : Uri?){
        Glide.with(this)
                .load(uri).into(binding.profileImage)
    }

}