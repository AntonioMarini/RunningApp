package com.apollyon.samproject.run

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.*
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

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

    //map screenshot
    private lateinit var mapScreenshot : Bitmap

    //km and time
    private var km : Double = 0.00
    private lateinit var lastLocation: Location

    private var startTime: Long = SystemClock.elapsedRealtime()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run_map, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        polylineOptions = PolylineOptions()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    val newPoint = LatLng(location.latitude, location.longitude)
                    updateDistance(LatLng(lastLocation.latitude, lastLocation.longitude), newPoint)
                    lastLocation = location
                    polylineOptions.add(newPoint).geodesic(true).color(R.color.primaryColor)
                    map.animateCamera(CameraUpdateFactory.newLatLng(newPoint))
                    map.addPolyline(polylineOptions)
                }
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.kmText.text = "km: 0.00"

        binding.stopButt.setOnClickListener{
            binding.chronometer.stop()
            map.snapshot(this)
        }
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


    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            permissionDenied = false
        }
        if(requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    map.isMyLocationEnabled = true
                    lastLocation = location
                    binding.kmText.text = String.format("km: %.2f", km)
                    binding.chronometer.start()
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),17f))
                }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(
                requireActivity() as AppCompatActivity, 1,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 6000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun updateDistance(oldPoint: LatLng, newPoint : LatLng){
        km += (SphericalUtil.computeDistanceBetween(oldPoint, newPoint) / 1000)
        binding.kmText.text = String.format("km: %.2f", km)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onSnapshotReady(bitmap: Bitmap?) {
        if (bitmap != null) {
            this.findNavController().navigate(RunMapFragmentDirections.actionRunMapFragmentToRunResultsFragment(bitmap,
                RunningSession()
            ))
        }
    }
}