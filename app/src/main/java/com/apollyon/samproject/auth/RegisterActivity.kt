package com.apollyon.samproject.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apollyon.samproject.MainActivity
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() , View.OnClickListener{

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        viewModel.email.observe(this, Observer { email->
            binding.emailEdit.setText(email)
        })

        viewModel.username.observe(this, Observer { username->
            binding.username.setText(username)
        })

        viewModel.age.observe(this, Observer { age->
            binding.age.setText(age.toString())
        })

        viewModel.password.observe(this, Observer { password->
            binding.password.setText(password)
        })

        viewModel.retypedPassword.observe(this, Observer { retypedPassword->
            binding.passwordEdit.setText(retypedPassword)
        })

        viewModel.userAdded.observe(this, Observer { userAdded ->
            if (userAdded){
                // go to main activity
                val intent =
                        Intent(this@RegisterActivity, MainActivity::class.java)
                Log.i("Register", "intent created...")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("user", viewModel.getFirebaseUser())
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@RegisterActivity,
                "Error during adding user, please try again",
                Toast.LENGTH_SHORT).show()
            }
        })

        binding.banner.setOnClickListener(this)
        binding.sendButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.banner -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.send_button -> register()
        }
    }

    private fun register() {
        binding.progressBar.visibility = View.VISIBLE
        if(validateForm()){
            viewModel.registerUser()
        }
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun validateForm() : Boolean{

        val email = binding.emailEdit.text.toString().trim()
        val username = binding.username.text.toString().trim()
        val age = binding.age.text.toString().trim()
        val password = binding.password.text.toString()
        val retypedPassword = binding.passwordEdit.text.toString()

        //passo i dati al viewModel per creare l'utente
        viewModel.setData(email,
            username,
            age.toInt(),
            password,
            retypedPassword
        )

        if(username.isEmpty()){
           binding.username.error = "username is required"
           binding.username.requestFocus()
            return false
        }

        if(age.isEmpty()){
            binding.age.error = "age is required"
            binding.age.requestFocus()
            return false
        }

        if(email.isEmpty()){
            binding.emailEdit.error = "email is required"
            binding.emailEdit.requestFocus()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEdit.error = "please provide valid email"
            binding.emailEdit.requestFocus()
            return false
        }

        if(password.isEmpty()){
            binding.password.error = "password is required"
            binding.password.requestFocus()
            return false
        }

        if(password.length < 6){
            binding.password.error = "password is too short"
            binding.password.requestFocus()
            return false
        }

        if(retypedPassword.isEmpty()){
            binding.passwordEdit.error = "write again your password"
            binding.passwordEdit.requestFocus()
            return false
        }

        if(!password.contentEquals(retypedPassword)){
            binding.passwordEdit.error = "password does not match"
            binding.passwordEdit.requestFocus()
            return false
        }

        return true

    }


}