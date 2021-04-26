package com.apollyon.samproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var logoutButt : Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //val users = Firebase.database.getReference("users")

        val logoutButton : Button = findViewById(R.id.logout_butt)

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.stats -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.new_session -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.trainer -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.profile -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }



    }

}