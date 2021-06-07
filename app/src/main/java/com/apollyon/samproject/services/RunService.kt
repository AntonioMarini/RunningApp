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
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.apollyon.samproject.ui.activities.MainActivity
import com.apollyon.samproject.R
import com.apollyon.samproject.utilities.RunUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A Foreground Service that holds locations data, time, distance,
 * to be shared with the app. It creates a notification
 */
class RunService : LifecycleService() {

    private var isFirstRun = true

    //used to get location of the user
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    // used to create(build) a new notification or update existing one
    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    // livedata that the RunFragment observe
    companion object{
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

        val timeRunInMillis = MutableLiveData<Long>()
        val distanceInMeters = MutableLiveData<Int>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }

    // initializes the livedata
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
        distanceInMeters.postValue(0)
    }

    /**
     *  Create a new empty polyline in the list of polyline,
     *  a polyline is a mutable list of Locations.
      */
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf()) // add an empty list
        pathPoints.postValue(this) // update the value of the livedata (it will be observed by the fragment)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf())) // if is null create a new (mutable) list of list of points

    /**
     *
     */
    private fun addPathPoint(location: Location?){
        location?.let{
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
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

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let { locations ->
                    for(location in locations){
                        addPathPoint(location)
                        updateDistance()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        currentNotificationBuilder = NotificationCompat.Builder(this, "running_channel")
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_trainer)
            .setContentTitle("Running App")
            .setContentText("00:00:00" + "  0.00 km")
            .setContentIntent(getRunActivityPendingIntent())

        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotification(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        startTimer() //resume
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    stopSelf()
                }
                else -> Log.i("Debug","unknown action")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L // time counter between start and pause (lap)
    private var runTimeCounter = 0L // total time counter,used to keep the time after pause
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    /**
     * Starts the timer, updates the livedata to inform the observer that the service
     * is now tracking, it uses coroutine (main dispatcher) to update the timer while the
     * service is tracking.
     */
    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //time difference between now and started time
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(runTimeCounter + lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    // aggiungo un secondo al tempo in secondi quando passano 1000 millisecond
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }

                delay(50L)
            }
            runTimeCounter += lapTime
        }
    }

    /**
     * Updates the distance livedata using the two last points.
     */
    private fun updateDistance(){
        // if the last polyline contains at least two points
        if(pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().size > 1) {
            val lastPoint = pathPoints.value!!.last().last()
            val preLastPoint = pathPoints.value!!.last()[pathPoints.value!!.last().size - 2]

            val result = FloatArray(1)
            Location.distanceBetween(
                preLastPoint.latitude, preLastPoint.longitude,
                lastPoint.latitude, lastPoint.longitude,
                result
            )

            distanceInMeters.postValue(distanceInMeters.value?.plus(result[0].toInt()))
        }
    }

    private fun startForegroundService(){
        startTimer()
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
            .setContentText("00:00:00" + "  0.00 km")
            .setContentIntent(getRunActivityPendingIntent())

        startForeground(1, notificationBuilder.build())

        timeRunInSeconds.observe(this, Observer { seconds ->
            val kilometers = distanceInMeters.value?.toDouble()?.times(0.001)
            val notification = currentNotificationBuilder
                .setContentText(RunUtil.getFormattedTime(seconds * 1000L) + String.format("  Distance: %.2f km", kilometers))
            notificationManager.notify(1, notification.build())
        })

    }

    private fun updateNotification(isTracking: Boolean){

        // buttons in the notification are based on currentstate of tracking
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent =if(isTracking){
            val pauseIntent = Intent(this, RunService::class.java).apply {
                action = "ACTION_PAUSE_SERVICE"
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            val resumeIntent = Intent(this, RunService::class.java).apply {
                action = "ACTION_START_OR_RESUME_SERVICE"
            }
            PendingIntent.getService(this, 1, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // removes action buttons in the notifications to add new one (so the row won't be filled)
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply{
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        currentNotificationBuilder
            .addAction(R.drawable.ic_pause, notificationActionText, pendingIntent)
        notificationManager.notify(1, currentNotificationBuilder.build())
    }

    private fun getRunActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = "ACTION_SHOW_RUN_MAP_FRAGMENT"

        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel("running_channel", "Running App", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Service", "destroyed")
        postInitialValues()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll() //distrugge tutte le notifiche (solo una)
    }

}