package com.apollyon.samproject.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentLoginBinding
import com.apollyon.samproject.viewmodels.LoginViewModel
import com.apollyon.samproject.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_forgot.*
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * LoginFragment contains the ui logic for the process of login
 * through Firebase Authentication (email and password)
 */
class LoginFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel : LoginViewModel
    private lateinit var binding : FragmentLoginBinding
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mainViewModel.onHideBars()
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        firebaseUser = Firebase.auth.currentUser

        // AUTOLOGIN
        if (firebaseUser != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            mainViewModel.onUserLogged(firebaseUser!!.uid)
            this.findNavController().navigate(

                LoginFragmentDirections.actionLoginFragmentToHome()
            )

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                firebaseUser =  Firebase.auth.currentUser
                mainViewModel.onUserLogged(viewModel.userId)

                this.findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToHome()
                )
            }else{
                Toast.makeText(
                    context,
                    "Error while logging in, please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        })


    }

    /**
     * Validates the form.
     * @return true if the form is correct, false o/w
     */
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

    /**
     * Simple function to give a feedback of errors to the user.
     */
    private fun inputError(textView: TextView, errorMessage : String){
        textView.error = errorMessage
        textView.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()

        // save the data in the viewmodel
        val email = binding.emailEditLogin.text.toString().trim()
        val password = binding.passwordEditLogin.text.toString()
        viewModel.setData(email, password)
    }

}