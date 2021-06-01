package com.apollyon.samproject.data

import com.apollyon.samproject.R

/**
 * data class that represent a training mode, which the user can choose
 */
data class TrainingMode(
        val image : Int = R.drawable.raccoon,
        val title : String,
        val description : String,
        var duration: Long = 0L,
        var distance: Float = 0.0f)