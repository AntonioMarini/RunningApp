package com.apollyon.samproject.run

import java.util.concurrent.TimeUnit

//contains utility methods for running tracker
class RunUtil {

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


}