package com.apollyon.samproject.ui.fragments

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
            setScaleEnabled(true);
            setDrawGridBackground(false);



            description.isEnabled= false
            xAxis.setDrawLabels(true)
            xAxis.textColor = android.graphics.Color.BLACK
            xAxis.axisMaximum = 6f
            xAxis.axisMinimum = 0f
            //xAxis.setDrawGridLines(false)

            legend.isEnabled = false

            //xAxis.axisLineWidth = 10f
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE;
            xAxis.textSize = 15f;
            xAxis.valueFormatter = RunChartFormatter()

            axisLeft.granularity = 0.01f
            axisLeft.setDrawGridLines(false)

            // set an alternative background color
            setBackgroundColor(android.graphics.Color.WHITE);

            //setViewPortOffsets(0f, 0f, 0f, 0f);
            setExtraOffsets(10f,10f,10f,10f)
        }

        mainViewModel.runSessions.observe(viewLifecycleOwner, Observer {
            setLineChartValues(it)
        })

    }


    private fun setLineChartValues(values : List<RunningSession>){

        for (session in values){
            entries.add(Entry(RunUtil.timeStampToChartX(session.timestamp), RunUtil.getDistanceKm(session.distanceInMeters).toFloat()))
        }


        lineDataSet = LineDataSet(entries, "Distance over time")
        lineDataSet.setDrawFilled(true)
        lineDataSet.setMode(if (lineDataSet.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
            LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER)

        linedata = LineData(lineDataSet)

        chart.data = linedata
        chart.invalidate() //refresh
    }

}