package com.apollyon.samproject.utilities

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * Class that contains utility methods for running
 */
object RunUtil {

    fun getFormattedTime(ms:Long, includeMillis: Boolean = false): String{
        var msCopy = ms

        val hours = TimeUnit.MILLISECONDS.toHours(msCopy)
        msCopy -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(msCopy)
        msCopy -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(msCopy)
        msCopy -= TimeUnit.SECONDS.toMillis(seconds)

        if(!includeMillis){
            return "${if(hours<10) "0" else ""}$hours:" +
                    "${if(minutes<10) "0" else ""}$minutes:"+
                    "${if(seconds<10) "0" else ""}$seconds"
        }
        msCopy /= 10 // rount to 3 decimals (centisecondi)
        return "${if(hours<10) "0" else ""}$hours:" +
                "${if(minutes<10) "0" else ""}$minutes:"+
                "${if(seconds<10) "0" else ""}$seconds:"+
                "${if(msCopy<10) "0" else ""}$msCopy"
    }

    fun getDistanceKm(meters: Int): Double {
        return meters.toDouble() * 0.001
    }

    fun calculateCalories(){

    }

    fun calculateAvgSpeedKmh(){

    }

    fun timeStampToChartX(timestamp : Long) : Float{
        val weekday = SimpleDateFormat("EEE").format(timestamp)
        var result :Float
        result = when(weekday){
            "Mon" -> 0f
            "Tue" -> 1f
            "Wed" -> 2f
            "Thu" -> 3f
            "Fry" -> 4f
            "Sat" -> 5f
            "Sun" -> 6f
            else -> -1f
        }
        return result
    }


}