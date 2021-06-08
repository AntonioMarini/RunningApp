package com.apollyon.samproject.utilities

import kotlin.math.pow

object LevelUtil {

    //Pokemon gen 1 level formula
    fun xpForNextLevel(level:Int) : Long{
        return ((4 * (level.toFloat().pow(3))) / 5).toLong()
    }

    fun calculateXpFromRun(calories: Int, avgSpeed: Float) : Long{
        return ((calories + avgSpeed) * 100).toLong()
    }

}