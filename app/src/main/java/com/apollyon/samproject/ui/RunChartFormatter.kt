package com.apollyon.samproject.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class RunChartFormatter : ValueFormatter() {

    private val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fry", "Sat", "Sun")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return days.getOrNull(value.toInt()) ?: value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return days.getOrNull(value.toInt()) ?: value.toString()
    }
}