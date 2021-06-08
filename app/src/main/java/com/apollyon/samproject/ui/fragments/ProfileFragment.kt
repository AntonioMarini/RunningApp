package com.apollyon.samproject.ui.fragments

import android.Manifest
import android.net.Uri
import android.os.Build
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
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class ProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentProfileBinding
    private var permissionsGranted = false

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
                ProfileFragmentDirections.actionProfileToLoginFragment()
            )
        }

        binding.profileImage.setOnClickListener {
            requestPermissions()
            if(permissionsGranted) {
                getContent.launch("image/*")
            }
        }

        return binding.root
    }

    private fun changePicture(uri : Uri?){
        Glide.with(this)
                .load(uri).into(binding.profileImage)
    }

    private fun requestPermissions() {
        if (PermissionsUtil.hasStoragePermissions(requireContext())) {
            permissionsGranted = true
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept storage access permissions to change your profile picture",
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        permissionsGranted = true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults, this)
    }

    companion object{
        const val REQUEST_CODE_LOCATION_PERMISSION = 98
    }

}