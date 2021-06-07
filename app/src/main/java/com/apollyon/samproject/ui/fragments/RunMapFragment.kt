package com.apollyon.samproject.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRunMapBinding
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.utilities.RunUtil
import com.apollyon.samproject.services.RunService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_run_map.*

class RunMapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var binding: FragmentRunMapBinding

    //map api
    private  var map: GoogleMap? = null
    private lateinit var polylineOptions: PolylineOptions

    //km and time
    private var curTimeInMillis = 0L
    private var metersDone : Int = 0

    private var isTracking =false
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

    private var session: RunningSession? = null

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

        // mi iscirvo al servizio
        subscribeToServiceObservers()

        binding.startButt.setOnClickListener {
            toggleRun() // starts the run
        }

        binding.stopButt.setOnClickListener{
            stopRun() // stops the run
        }

        addAllPolylines()
    }

    //
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            map = googleMap
        }
        addAllPolylines()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
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

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(10f)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
            polylineOptions
        }
    }

    private fun animateCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), 17f))
        }
    }

    /**
        Listen to the observable livedata in the RunService
     */
    private fun subscribeToServiceObservers(){

        RunService.isTracking.observe(viewLifecycleOwner, Observer { tracking ->
            getPointsFromService(tracking)
        })

        RunService.pathPoints.observe(viewLifecycleOwner, Observer { points ->
            pathPoints = points
            addLatestPolyline()
            animateCameraToUser()
        })

        RunService.distanceInMeters.observe(viewLifecycleOwner, Observer { meters ->
            metersDone = meters
            val km = meters.toDouble() * 0.001
            km_text.text = String.format("Distance: %.2f km", km)
        })

        RunService.timeRunInMillis.observe(viewLifecycleOwner, Observer { millis ->
            curTimeInMillis = millis
            val formattedTime = RunUtil.getFormattedTime(curTimeInMillis, true)
            chronometer.text = formattedTime
        })

        binding.kmText.text = "Distance: 0.00 km"
        binding.chronometer.text = "00:00:00:00"
    }

    /**
     *  send an action to the run service
     *  @param action : action to send to the service
     */
    private fun sendCommandToService(action: String){
        Intent(requireContext(), RunService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService("ACTION_PAUSE_SERVICE")
        }else{
            sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
        }
    }

    private fun stopRun() {
        sendCommandToService("ACTION_STOP_SERVICE")

        session = RunningSession(
            distanceInMeters = metersDone,
            timeMilli = curTimeInMillis,
            timestamp = System.currentTimeMillis(),
            avgSpeedInKMH = RunUtil.calculateAvgSpeedKmh(metersDone, curTimeInMillis),
            caloriesBurned = RunUtil.calculateCalories(metersDone, 70f)
        ) //TODO weight non hardcoded

        zoomToSeeWholeTrack()

        map?.snapshot { bitmap->
            // navigate to the RunResults Fragment
            if (bitmap != null) {
                this.findNavController().navigate(
                    RunMapFragmentDirections.actionRunMapFragmentToRunResultsFragment(
                        mapScreenBitmap = bitmap,
                        session = session
                ))
            }
        }
    }

    private fun getPointsFromService(isTracking : Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            start_butt.text = "Start"
            stop_butt.visibility = View.VISIBLE
        }else{
            start_butt.text = "Pause"
            stop_butt.visibility = View.GONE
        }
    }

    /**
     * Add the latest polyline that the fragment observes from the service
     */
    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            polylineOptions = PolylineOptions()
                .color(R.color.color3)
                .width(10f)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

}