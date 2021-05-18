package com.apollyon.samproject.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel : LoginViewModel
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        val user = viewModel.auth.currentUser

        if (user != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            this.findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToMainActivity()
            )
            requireActivity().finish()
        }

       binding.forgetPassword.setOnClickListener {
           this.findNavController().navigate(
               LoginFragmentDirections.actionLoginFragmentToForgotFragment()
           )
        }

        binding.registrateLabel.setOnClickListener {
            this.findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment2()
            )
        }

        binding.loginButton.setOnClickListener {
            if (validateForm()) viewModel.login()
        }

        viewModel.userLogged.observe(viewLifecycleOwner, Observer { userLogged ->
            if (userLogged){
                this.findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainActivity()
                )
                requireActivity().finish()
            }else{
                Toast.makeText(
                    context,
                    "Error while logging in, please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    private fun validateForm(): Boolean{

        val emailEdit = binding.emailEditLogin
        val passwordEdit = binding.passwordEditLogin

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