package com.apollyon.samproject.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.apollyon.samproject.R
import com.apollyon.samproject.data.TrainingMode
import com.apollyon.samproject.databinding.FragmentNewSessionBinding
import com.apollyon.samproject.ui.adapters.TrainingsAdapter
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.viewmodels.SessionViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class NewSessionFragment : Fragment(), ViewPager.OnPageChangeListener, EasyPermissions.PermissionCallbacks{

    private lateinit var viewPager: ViewPager
    private lateinit var trainingsAdapter: TrainingsAdapter

    private val mainViewModel : MainViewModel by activityViewModels()

    private var permissionsGranted = false

    private lateinit var binding : FragmentNewSessionBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_session, container, false)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        requestPermissions()

        val sessionViewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        binding.sessionViewModel = sessionViewModel

        val trainingModes = ArrayList<TrainingMode>()
        trainingModes.add(TrainingMode(R.drawable.raccoon, "Standard Running", "This is pretty normal"))
        trainingModes.add(TrainingMode(R.drawable.ghepardo, "HIIT Running", "High-intensity interval training running consists of intervals of high intensive run with lower intensive intervals"))
        trainingModes.add(TrainingMode(R.drawable.falco, "Cycling", "Bike is nice"))

        trainingsAdapter = TrainingsAdapter(trainingModes, context)

        viewPager = binding.pager
        viewPager.adapter = trainingsAdapter
        viewPager.setPadding(130, 0, 130, 0)

        binding.buttonStart.setOnClickListener {
            if (permissionsGranted) {
                this.findNavController()
                    .navigate(NewSessionFragmentDirections.actionNewSessionToRunMapFragment())
            }
            else
                requestPermissions()
        }

        viewPager.addOnPageChangeListener(this)


        return binding.root
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            ;
    }

    override fun onPageSelected(position: Int) {
            ;
    }

    override fun onPageScrollStateChanged(state: Int) {
            ;
    }

    private fun requestPermissions() {
        if (PermissionsUtil.hasLocationPermissions(requireContext())) {
            permissionsGranted = true
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
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
        const val REQUEST_CODE_LOCATION_PERMISSION = 99
    }


}