package com.apollyon.samproject.ui.fragments

import android.graphics.Paint
import android.icu.text.Transliterator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.apollyon.samproject.R
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.databinding.FragmentStatsBinding
import com.apollyon.samproject.ui.RunChartFormatter
import com.apollyon.samproject.utilities.RunUtil
import com.apollyon.samproject.viewmodels.MainViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentStatsBinding


    val entries = mutableListOf<Entry>()
    lateinit var lineDataSet: LineDataSet
    lateinit var linedata: LineData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_stats, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart.apply {
            setTouchEnabled(false)
            setScaleEnabled(true)
            setScaleEnabled(true)
            setDrawGridBackground(false)
            setExtraOffsets(10f,10f,10f,10f)
            description.text = "Average Speed"
            description.textSize = 12f

            legend.isEnabled = false
        }

        chart.axisLeft.apply {
            setDrawGridLines(false)
        }

        chart.axisRight.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }

        chart.xAxis.apply {
            setDrawLabels(false)
            textColor = android.graphics.Color.BLACK
            axisMaximum = 6f
            axisMinimum = 0f
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM_INSIDE;
            textSize = 15f
            valueFormatter = RunChartFormatter()
        }

        mainViewModel.runSessions.observe(viewLifecycleOwner, Observer {
            setLineChartValues(it)
        })

    }

    private fun setLineChartValues(values : List<RunningSession>){

        for (session in values){
            entries.add(Entry(RunUtil.timeStampToChartX(session.timestamp),session.avgSpeedInKMH))
        }

        lineDataSet = LineDataSet(entries, "Distance over time")
        lineDataSet.setDrawFilled(true)
        lineDataSet.valueTextSize = 15f
        lineDataSet.setMode(if (lineDataSet.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
            LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER)

        linedata = LineData(lineDataSet)

        chart.data = linedata
        chart.invalidate() //refresh
    }

}