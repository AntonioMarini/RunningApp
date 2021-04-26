package com.apollyon.samproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
private lateinit var sendButt : Button
private lateinit var emailEdit : EditText

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = Firebase.auth
        sendButt = findViewById(R.id.send_button)
        emailEdit = findViewById(R.id.email_edit)


        sendButt.setOnClickListener {

            val email = emailEdit.text.toString().trim()


            if(email.isEmpty()){
                emailEdit.error = "email is required"
                emailEdit.requestFocus()
            }else  if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEdit.error = "please provide valid email"
                emailEdit.requestFocus()
            }else {
                sendEmail(email)
            }
        }

    }

    private fun sendEmail(email : String) {
        var esit : String = ""
        if(checkEmail(email)){
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    esit = "Email sent"
                }else{
                    esit = "Error sending reset email."
                }
            }
        }else{
            esit = "Email is not registered"
        }
        Toast.makeText(
            this@ForgotPasswordActivity,
            esit,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkEmail(email  : String) : Boolean{
        var existent = false
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            existent = task.result!!.signInMethods!!.isNotEmpty()
        }
        return existent
    }
}