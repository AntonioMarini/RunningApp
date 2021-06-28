package com.apollyon.samproject.data

import android.os.Parcelable
import com.apollyon.samproject.R
import kotlinx.android.parcel.Parcelize

/**
 * data class that represent a training mode, which the user can choose
 */
@Parcelize
data class TrainingMode(
        val image : Int = R.drawable.raccoon,
        val title : String,
        val description : String,
        var duration: Long = 0L,
        var distance: Float = 0.0f) : Parcelable