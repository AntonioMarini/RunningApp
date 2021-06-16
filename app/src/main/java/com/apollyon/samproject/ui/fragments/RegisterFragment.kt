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
import com.apollyon.samproject.databinding.FragmentRegisterBinding
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.viewmodels.RegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * RegisterFragment contains the ui logic for the process of registration
 * through Firebase Authentication (email and password)
 */
class RegisterFragment : Fragment() , View.OnClickListener{

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    private val mainViewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        //initializes the binding and the viewmodel
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_register, container, false )
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.registerViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observe the livedata in the viewmodel to see if the user has been correctly registered
        viewModel.userAdded.observe(viewLifecycleOwner, Observer { userAdded ->
            if (userAdded){
                mainViewModel.onUserLogged(viewModel.uid!!)
                mainViewModel.insertNewUserLocal(viewModel.user)

                // go to main activity
                this.findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToHome()
                )
                //requireActivity().finish()
            }else{
                // give feedback error to the user
                Toast.makeText(context,
                    "Error during adding user, please try again",
                    Toast.LENGTH_SHORT).show()
            }
        })

        binding.registerButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.banner -> findNavController().popBackStack() // navigate to login (back button)
            R.id.register_button -> register() // tries to register new user
        }
    }

    /**
     * Wrapper function that calls viewmodel's register func.
     */
    private fun register() {
        if(validateForm()){
            viewModel.registerUser()
        }
    }

    private fun setViewmodelData(){

        val email = binding.emailEditReg.text.toString().trim()
        val username = binding.usernameReg.text.toString().trim()
        val age = binding.ageEdit.text.toString().trim()
        val height = binding.heightEdit.text.toString().trim()
        val weight = binding.weightEdit.text.toString().trim()
        val password = binding.passwordEditReg.text.toString()
        val retypedPassword = binding.passwordEditReg2.text.toString()

        //passo i dati al viewModel per creare l'utente
        viewModel.setData(
            email = email,
            username = username,
            age = age,
            height = height,
            weight = weight,
            password = password,
            retypedPassword = retypedPassword
        )

    }

    /**
     * Validates the form.
     * @return true if the form is correct, false o/w
     */
    private fun validateForm() : Boolean{

        val email = binding.emailEditReg.text.toString().trim()
        val username = binding.usernameReg.text.toString().trim()
        val age = binding.ageEdit.text.toString().trim()
        val height = binding.heightEdit.text.toString().trim()
        val weight = binding.weightEdit.text.toString().trim()
        val password = binding.passwordEditReg.text.toString()
        val retypedPassword = binding.passwordEditReg2.text.toString()

        if(username.isEmpty()){
           binding.usernameReg.error = "username is required"
           binding.usernameReg.requestFocus()
            return false
        }

        if(age.isEmpty()){
            binding.ageEdit.error = "age is required"
            binding.ageEdit.requestFocus()
            return false
        }

        if(email.isEmpty()){
            binding.emailEditReg.error = "email is required"
            binding.emailEditReg.requestFocus()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEditReg.error = "please provide valid email"
            binding.emailEditReg.requestFocus()
            return false
        }

        if(password.isEmpty()){
            binding.passwordEditReg.error = "password is required"
            binding.passwordEditReg.requestFocus()
            return false
        }

        if(password.length < 6){
            binding.passwordEditReg.error = "password is too short"
            binding.passwordEditReg.requestFocus()
            return false
        }

        if(retypedPassword.isEmpty()){
            inputError(password_edit_reg2, "write again your password")
            return false
        }

        if(!password.contentEquals(retypedPassword)){
            inputError(password_edit_reg2, "password does not match")
            return false
        }

        //passo i dati al viewModel per creare l'utente
        viewModel.setData(
            email,
            username,
            age,
            height,
            weight,
            password,
            retypedPassword
        )

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
        setViewmodelData() // save the datain the viewmodel
    }

}