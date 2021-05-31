package com.apollyon.samproject.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentProfileBinding

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        if (uri != null) {
            mainViewModel.uploadImage(uri)
        } // TODO else lancia errore di caricamento
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        if(mainViewModel.authUser?.photoUrl != null){
                changePicture(mainViewModel.authUser?.photoUrl)
            }

        mainViewModel.profileImageDownloaded.observe(viewLifecycleOwner, Observer {uri ->
            changePicture(uri)
        })

        binding.buttonLogout.setOnClickListener {
            Firebase.auth.signOut()
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileToAuthActivity()
            )
            requireActivity().finish()
        }

        binding.profileImage.setOnClickListener {
           getContent.launch("image/*")
        }

        return binding.root
    }

    private fun changePicture(uri : Uri?){
        Glide.with(this)
                .load(uri).into(binding.profileImage)
    }

}