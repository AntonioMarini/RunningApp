package com.apollyon.samproject.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollyon.samproject.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel(){

    private var database : FirebaseDatabase = Firebase.database
    private var auth : FirebaseAuth? = null
    private lateinit var firebaseUser : FirebaseUser

    // encapsulated livedata fields

    private val _userAdded = MutableLiveData<Boolean>()
    val userAdded : LiveData<Boolean>
        get() = _userAdded

    private val _email = MutableLiveData<String>("")
    val email : LiveData<String>
        get() = _email

    private val _username = MutableLiveData<String>("")
    val username : LiveData<String>
        get() = _username

    private val _age = MutableLiveData<Int>(18)
    val age : LiveData<Int>
        get() = _age

    private val _password = MutableLiveData<String>("")
    val password : LiveData<String>
        get() = _password

    private val _retypedPassword = MutableLiveData<String>("")
    val retypedPassword : LiveData<String>
        get() = _retypedPassword

    //constructor

    init{
        auth = FirebaseAuth.getInstance()
        _username.value = ""
        _email.value = ""
        _password.value = ""
        _retypedPassword.value = ""
    }

    fun setData(email : String, username : String, age : Int, password: String, retypedPassword: String){
        _email.value =  email
        _username.value = username
        _age.value = age
        _password.value = password
        _retypedPassword.value = retypedPassword
    }

    // returns true if user has been succesfully signed up in firebase, false o/w
    fun registerUser() {

        auth?.createUserWithEmailAndPassword(_email.value,_password.value)
            ?.addOnCompleteListener( OnCompleteListener<AuthResult> { task ->
                //if the registration is succesfully done
                if (task.isSuccessful){
                    firebaseUser = task.result!!.user!!
                    //val defaultUri = Uri.parse("android.resource://com.apollyon.samproject/drawable/raccoon.jpg");
                    val user : User = User(firebaseUser.uid, _email.value, _username.value, _age.value)
                    addUser(user, firebaseUser)
                    _userAdded.value = true
                }else{
                    _userAdded.value = false
                }
            })
    }

    private fun addUser(user: User, firebaseUser: FirebaseUser){
        val myRef = database.getReference("users")
        myRef.child(firebaseUser.uid).setValue(user)
    }

    fun getFirebaseUser() : FirebaseUser{
        return firebaseUser
    }

    override fun onCleared() {
        super.onCleared()
    }

}