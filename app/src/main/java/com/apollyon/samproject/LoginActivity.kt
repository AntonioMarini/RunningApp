package com.apollyon.samproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase




class LoginActivity : AppCompatActivity(){


    private lateinit var auth: FirebaseAuth
    private lateinit var emailEdit :EditText
    private lateinit var passwordEdit :EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser


        if (user != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java);
            intent.putExtra("user", user)
            startActivity(intent);
            finish();
        }
        //TODO fai mettere email a cui spedire la password

        val forget_butt : TextView = findViewById(R.id.forget_password)
        forget_butt.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java);
            startActivity(intent);
        }

        val register : TextView = findViewById(R.id.registrate_label)
        register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        val loginButton = findViewById<Button>(R.id.send_button)
        loginButton.setOnClickListener {
            login()
        }

    }

    private fun login() {
        //validate form
        emailEdit = findViewById(R.id.email_edit)
        passwordEdit = findViewById(R.id.passwordEdit)

        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()

        if(email.isEmpty()){
            emailEdit.error = "email is required"
            emailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.error = "please provide valid email"
            emailEdit.requestFocus()
            return
        }

        if(password.isEmpty()){
            passwordEdit.error = "password is required"
            passwordEdit.requestFocus()
            return
        }


        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra(
                        "user",
                        FirebaseAuth.getInstance().currentUser
                    )
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }




}