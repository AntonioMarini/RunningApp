package com.apollyon.samproject

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


private lateinit var banner : TextView
private lateinit var registerUser : Button

private lateinit var emailEdit : EditText
private lateinit var usernameEdit : EditText
private lateinit var ageEdit : EditText

private lateinit var passwordEdit : EditText
private lateinit var retypePasswordEdit : EditText

private lateinit var progressBar : ProgressBar

private var mAuth: FirebaseAuth? = null
private var database : FirebaseDatabase? = null

class Register : AppCompatActivity() , View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        banner = findViewById(R.id.banner)
        banner.setOnClickListener(this)

        registerUser = findViewById(R.id.send_button)
        registerUser.setOnClickListener(this)

        ageEdit = findViewById(R.id.age)
        usernameEdit = findViewById(R.id.username)
        emailEdit = findViewById(R.id.email_edit)
        passwordEdit = findViewById(R.id.password)
        retypePasswordEdit = findViewById(R.id.passwordEdit)

        progressBar = findViewById(R.id.progressBar)

        database = Firebase.database


    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.banner -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.send_button -> register()
        }
    }

    private fun register() {
        if(validateForm()){
            progressBar.visibility = View.VISIBLE
            register_user()
        }
    }

    private fun validateForm() : Boolean{

        val email = emailEdit.text.toString().trim()
        val username = usernameEdit.text.toString().trim()
        val age = ageEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        val retypedPassword = retypePasswordEdit.text.toString()

        if(username.isEmpty()){
            usernameEdit.error = "username is required"
            usernameEdit.requestFocus()
            return false
        }

        if(age.isEmpty()){
            ageEdit.error = "age is required"
            ageEdit.requestFocus()
            return false
        }

        if(email.isEmpty()){
            emailEdit.error = "email is required"
            emailEdit.requestFocus()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.error = "please provide valid email"
            emailEdit.requestFocus()
            return false
        }

        if(password.isEmpty()){
            passwordEdit.error = "password is required"
            passwordEdit.requestFocus()
            return false
        }

        if(password.length < 6){
            passwordEdit.error = "password is too short"
            passwordEdit.requestFocus()
            return false
        }

        if(retypedPassword.isEmpty()){
            retypePasswordEdit.error = "write again your password"
            retypePasswordEdit.requestFocus()
            return false
        }

        if(!password.contentEquals(retypedPassword)){
            retypePasswordEdit.error = "password does not match"
            retypePasswordEdit.requestFocus()
            return false
        }

        return true

    }

    private fun register_user(){

        val email = emailEdit.text.toString().trim()
        val username = usernameEdit.text.toString().trim()
        val age = ageEdit.text.toString().toInt()
        val password = passwordEdit.text.toString()

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( OnCompleteListener<AuthResult> { task ->
                    //if the registration is succesfully done
                    if (task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        Toast.makeText(
                                this@Register,
                                "You are registered succesfully.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val user : User = User(email, username, age)
                        add_User(user, firebaseUser)

                        val intent =
                                Intent(this@Register, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("user", firebaseUser)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                                this@Register,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }

    private fun add_User(user: User, firebaseUser: FirebaseUser){
        val myRef = database!!.getReference("users")
        myRef.child(firebaseUser.uid).setValue(user).addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Toast.makeText(
                        this@Register,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}