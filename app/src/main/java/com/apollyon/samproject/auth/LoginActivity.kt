package com.apollyon.samproject.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.MainActivity
import com.apollyon.samproject.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity(){

    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        val user = viewModel.auth.currentUser

        if (user != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }

        binding.forgetPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java);
            startActivity(intent);
        }

        binding.registrateLabel.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.sendButton.setOnClickListener {
            if (validateForm()) viewModel.login()
        }

        viewModel.userLogged.observe(this, Observer { userLogged ->
            if (userLogged){
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra(
                        "user",
                        viewModel.auth.currentUser
                )
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(
                        this@LoginActivity,
                        "Error while logging in, please try again",
                        Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun validateForm(): Boolean{

        val emailEdit = binding.emailEdit
        val passwordEdit = binding.passwordEdit

        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()

        if(email.isEmpty()){
            inputError(emailEdit,"email is required")
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputError(emailEdit,"please provide a valid email")
            return false
        }

        if(password.isEmpty()){
            inputError(passwordEdit,"password is required")
            return false
        }

        viewModel.setData(email, password)

        return true
    }

    private fun inputError(textView: TextView, errorMessage : String){
        textView.error = errorMessage
        textView.requestFocus()
    }

}