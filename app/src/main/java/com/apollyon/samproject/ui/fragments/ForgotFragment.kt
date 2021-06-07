package com.apollyon.samproject.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentForgotBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : FragmentForgotBinding
    //private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_forgot, container, false)
        //viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)



        binding.sendButt.setOnClickListener {

            val email = binding.emailEdit.text.toString().trim()


            if(email.isEmpty()){
                binding.emailEdit.error = "email is required"
                binding.emailEdit.requestFocus()
            }else  if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailEdit.error = "please provide valid email"
                binding.emailEdit.requestFocus()
            }else {
                sendEmail(email)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
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
        //Toast.makeText(
            //this@ForgotPasswordActivity,
            //esit,
           //Toast.LENGTH_SHORT
       // ).show()
    }

    private fun checkEmail(email  : String) : Boolean{
        var existent = false
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            existent = task.result!!.signInMethods!!.isNotEmpty()
        }
        return existent
    }
}