package com.apollyon.samproject.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() , View.OnClickListener{

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_register, container, false )
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        viewModel.email.observe(viewLifecycleOwner, Observer { email->
            binding.emailEditReg.setText(email)
        })

        viewModel.username.observe(viewLifecycleOwner, Observer { username->
           binding.usernameReg.setText(username)
        })

        viewModel.age.observe(viewLifecycleOwner, Observer { age->
            binding.ageEdit.setText(age.toString())
        })

        viewModel.password.observe(viewLifecycleOwner, Observer { password->
            binding.passwordEditReg.setText(password)
        })

        viewModel.retypedPassword.observe(viewLifecycleOwner, Observer { retypedPassword->
            binding.passwordEditReg2.setText(retypedPassword)
        })

        viewModel.userAdded.observe(viewLifecycleOwner, Observer { userAdded ->
            if (userAdded){
                // go to main activity
                this.findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToMainActivity()
                )
                requireActivity().finish()
            }else{
                Toast.makeText(context,
                    "Error during adding user, please try again",
                    Toast.LENGTH_SHORT).show()
            }
        })

        binding.registerButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)



        return binding.root
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            //R.id.banner -> startActivity(Intent(this, StartActivity::class.java))
            R.id.register_button -> register()
        }
    }

    private fun register() {
        //binding.progressBar.visibility = View.VISIBLE
        if(validateForm()){
            viewModel.registerUser()
        }
        //binding.progressBar.visibility = View.INVISIBLE
    }

    private fun validateForm() : Boolean{

        val email = binding.emailEditReg.text.toString().trim()
        val username = binding.usernameReg.text.toString().trim()
        val age = binding.ageEdit.text.toString().trim()
        val password = binding.passwordEditReg.text.toString()
        val retypedPassword = binding.passwordEditReg2.text.toString()

        //passo i dati al viewModel per creare l'utente
        viewModel.setData(email,
            username,
            age.toInt(),
            password,
            retypedPassword
        )

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
            binding.passwordEditReg2.requestFocus()
            return false
        }

        if(password.length < 6){
            binding.passwordEditReg.error = "password is too short"
            binding.passwordEditReg.requestFocus()
            return false
        }

        if(retypedPassword.isEmpty()){
            binding.passwordEditReg2.error = "write again your password"
            binding.passwordEditReg2.requestFocus()
            return false
        }

        if(!password.contentEquals(retypedPassword)){
            binding.passwordEditReg2.error = "password does not match"
            binding.passwordEditReg2.requestFocus()
            return false
        }

        return true

    }


}