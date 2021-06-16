package com.apollyon.samproject.ui

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressAnimation(var progressBar: ProgressBar, var from: Int, var to: Int) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }
}