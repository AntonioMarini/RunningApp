package com.apollyon.samproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.core.view.iterator
import androidx.fragment.app.*
import com.google.android.material.bottomnavigation.BottomNavigationView

private lateinit var logoutButt : Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //val users = Firebase.database.getReference("users")

        //TODO move this in profile fragment
        /*
        val logoutButton : Button = findViewById(R.id.logout_butt)

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }*/
        if (savedInstanceState == null) {

            val fragment = HomeFragment.newInstance("","")
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment, "home")
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit()

        }


        //print(fragment.tag.toString())


        val bottomNavigationView : BottomNavigationView= findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    val fragment = HomeFragment.newInstance("","")
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

                    val fragment = ProfileFragment.newInstance("","")
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