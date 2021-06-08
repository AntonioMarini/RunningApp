package com.apollyon.samproject.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel(){

    private val _userLogged = MutableLiveData<Boolean>()
    val userLogged : LiveData<Boolean>
        get() = _userLogged

    private val _email = MutableLiveData<String>()
    val email : LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String>
        get() = _password

    var userId : String  = ""

    init {
        _email.value = ""
        _password.value = ""
    }

    /**
     * Use Firebase auth to login.
     */
    fun login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(_email.value,_password.value)
                .addOnCompleteListener { task ->
                    _userLogged.value = task.isSuccessful
                    userId = task.result.user.uid
                    Log.i("UID", userId.toString())
                }
    }

    fun setData(email : String = "", password: String = ""){
        _email.value = email
        _password.value = password
    }


}