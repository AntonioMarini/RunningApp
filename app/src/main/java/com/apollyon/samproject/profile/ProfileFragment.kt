package com.apollyon.samproject.profile

import android.app.Activity
import android.app.Notification
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.apollyon.samproject.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.auth.LoginActivity
import com.apollyon.samproject.databinding.FragmentProfileBinding
import com.apollyon.samproject.datastruct.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_profile,
                container,
                false
        )

        val buttonLogout : Button = binding.buttonLogout
        val buttonResetPass : Button = binding.buttonResetpass
        val profileImage : CircleImageView = binding.profileImage
        val usernameText : TextView = binding.usernameLabel


        if(mainViewModel.authUser?.photoUrl != null){
            Glide.with(this)
                    .load(mainViewModel.authUser?.photoUrl).into(profileImage)
            }else{
                Log.d(TAG,"userUri is null")
            }

        mainViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            usernameText.text = user.username
        })

        mainViewModel.profileImageDownloaded.observe(viewLifecycleOwner, Observer {uri ->
            Glide.with(this)
                    .load(uri).into(profileImage)
        })

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            if (uri != null) {
                mainViewModel.uploadImage(uri)
            } // TODO else lancia errore di caricamento
        }

        buttonLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        profileImage.setOnClickListener {
           getContent.launch("image/*")

        }

        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
            }
    }
}