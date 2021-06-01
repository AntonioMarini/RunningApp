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

/**
 * Viewmodel that holds all the logic to register a new user, add it to the firebase auth,
 * and the firebase realtime, wich stores info about the user like the username, age, etc..
 * This viewmodel contains livedata that the RegisterFragment observe.
 */
class RegisterViewModel : ViewModel(){

    private var database : FirebaseDatabase = Firebase.database
    private var auth : FirebaseAuth? = FirebaseAuth.getInstance()
    private lateinit var firebaseUser : FirebaseUser

    //well encapsulated livedata fields that contains the data of the form
    // they also prevent to lose the data when the fragment associated is destroyed
    // (e.g. when the screen is rotated)

    private val _userAdded = MutableLiveData<Boolean>()
    val userAdded : LiveData<Boolean> get() = _userAdded

    private val _email = MutableLiveData("")
    val email : LiveData<String> get() = _email

    private val _username = MutableLiveData("")
    val username : LiveData<String> get() = _username

    private val _age = MutableLiveData("")
    val age : LiveData<String> get() = _age

    private val _password = MutableLiveData("")
    val password : LiveData<String> get() = _password

    private val _retypedPassword = MutableLiveData("")
    val retypedPassword : LiveData<String> get() = _retypedPassword

    /**
     * set the livedata of the viewmodel
     */
    fun setData(email : String = "", username : String = "", age : String = "" , password: String = "", retypedPassword: String = ""){
        _email.value =  email
        _username.value = username
        _age.value = age
        _password.value = password
        _retypedPassword.value = retypedPassword
    }

    /**
     * Calls the firebase auth api to register the user.
     * @return true if user has been successfully signed up in firebase, false o/w
      */
    fun registerUser() {
        auth?.createUserWithEmailAndPassword(_email.value.toString(),_password.value.toString())
            ?.addOnCompleteListener( OnCompleteListener<AuthResult> { task ->
                //if the registration is successful
                if (task.isSuccessful){
                    firebaseUser = task.result!!.user!!
                    val user : User = User(firebaseUser.uid, _email.value, _username.value,
                        _age.value?.toInt()
                    )
                    addUser(user, firebaseUser.uid)
                    _userAdded.value = true
                }else{
                    _userAdded.value = false
                }
            })
    }

    /**
     * Add the user in the firebase realtime database (for data associated with the user)
     * @param user The user to be added.
     * @param firebaseUserUid The uid to access the user data
     */
    private fun addUser(user: User, firebaseUserUid: String){
        val myRef = database.getReference("users")
        myRef.child(firebaseUserUid).setValue(user)
    }


    fun getFirebaseUser() : FirebaseUser{
        return firebaseUser
    }

    override fun onCleared() {
        super.onCleared()
    }

}