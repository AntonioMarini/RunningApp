package com.apollyon.samproject.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.apollyon.samproject.R
import com.apollyon.samproject.RunActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.maps.model.LatLng

class RunService : LifecycleService() {

    private var isFirstRun = true

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf()) // add an empty list
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?){
        location?.let{
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        if(isTracking){
            val request = LocationRequest.create().apply {
                interval = 5000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let { locations ->
                    for(location in locations){
                        addPathPoint(location)
                        Log.i("location: ", "${location.longitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action){
                "ACTION_START_OR_RESUME_SERVICE" -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        Log.i("SERVICE","resuming service")
                    }
                }
                "ACTION_PAUSE_SERVICE" -> Log.i("AAAAAAAAAAAAA","pause action")
                "ACTION_STOP_SERVICE" -> Log.i("AAAAAAAAAAAAA","stop action")
                else -> Log.i("AAAAAAAAAAAAA","unknown action")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "running_channel")
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_trainer)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getRunActivityPendingIntent())

        startForeground(1, notificationBuilder.build())

    }

    private fun getRunActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, RunActivity::class.java).also {
            it.action = "ACTION_SHOW_RUN_MAP_FRAGMENT"
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel("running_channel", "Running App", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}