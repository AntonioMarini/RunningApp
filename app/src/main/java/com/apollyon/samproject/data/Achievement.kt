package com.apollyon.samproject.data

import android.graphics.Bitmap

data class Achievement(
    private val name: String,

    private val description: String,

    private val imageBmp: Bitmap,

    private var obtained: Boolean = false
)
