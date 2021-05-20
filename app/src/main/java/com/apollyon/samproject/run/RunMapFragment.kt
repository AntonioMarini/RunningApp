package com.apollyon.samproject.run

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.PermissionUtils.isPermissionGranted
import com.apollyon.samproject.PermissionUtils.requestPermission
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRunMapBinding
import com.apollyon.samproject.datastruct.RunningSession
import com.apollyon.samproject.services.RunService
import com.google.android.gms.location.*
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.fragment_run_map.*
import kotlinx.android.synthetic.main.fragment_run_map.view.*

class RunMapFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.SnapshotReadyCallback {

    private lateinit var binding: FragmentRunMapBinding

    // permission for tracing user
    private var permissionDenied = false

    //map api
    private lateinit var map: GoogleMap
    private lateinit var polylineOptions: PolylineOptions

    // Location updates to show on map
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates : Boolean = true

    //km and time
    private var runPaused : Boolean = false
    private var km : Double = 0.00
    private lateinit var lastLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run_map, container, false)

        polylineOptions = PolylineOptions()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.kmText.text = "km: 0.00"

        binding.startButt.setOnClickListener {
            binding.chronometer.start()
            sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
            runPaused = false
        }

        binding.pauseButt.setOnClickListener {
            binding.chronometer.stop()
            runPaused = true
        }

        binding.stopButt.setOnClickListener{
            binding.chronometer.stop()
            map.snapshot(this)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        if (permissionDenied) {
            permissionDenied = false
        }
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMyLocationClick(location : Location) {
        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != 1) {
            return
        }
        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(
                requireActivity() as AppCompatActivity, 1,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
    }

    fun updateDistance(oldPoint: LatLng, newPoint : LatLng){
        if(!runPaused) {
            km += (SphericalUtil.computeDistanceBetween(oldPoint, newPoint) / 1000)
            binding.kmText.text = String.format("km: %.2f", km)
        }
    }

    override fun onSnapshotReady(bitmap: Bitmap?) {
        if (bitmap != null) {
            this.findNavController().navigate(RunMapFragmentDirections.actionRunMapFragmentToRunResultsFragment(bitmap,
                RunningSession()
            ))
        }
    }

    private fun sendCommandToService(action: String){
        Intent(requireContext(), RunService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            map = googleMap
        }
        enableMyLocation()
    }
}