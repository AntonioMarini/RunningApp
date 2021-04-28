package com.apollyon.samproject

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.iterator
import androidx.fragment.app.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.snapshot.PriorityIndex

class MainActivity : AppCompatActivity() {

    private var database : FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val firebaseUser : FirebaseUser = intent.extras?.get("user") as FirebaseUser
        val userReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        var user : User? = null

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue<User>()!!
                if (savedInstanceState == null) {

                    val fragment = HomeFragment.newInstance(user?.username)
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment_container_view, fragment, "home")
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit()

                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        userReference.addValueEventListener(userListener)





        Log.println(Log.INFO, null, user?.username.toString())
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_cont)
        //setSupportActionBar(toolbar)



        val bottomNavigationView : BottomNavigationView= findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    val fragment = HomeFragment.newInstance(user?.username)
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