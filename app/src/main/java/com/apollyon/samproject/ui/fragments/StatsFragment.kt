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
import com.apollyon.samproject.databinding.FragmentStatsBinding
import com.apollyon.samproject.viewmodels.MainViewModel
import java.text.SimpleDateFormat

class StatsFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentStatsBinding

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
        mainViewModel.totalKm.observe(viewLifecycleOwner, Observer {
            if(it!=null)
                binding.distanceText.text = "Total distance: ${it} km"
            else
                binding.distanceText.text = "Total distance: ${0} km"
        })

        mainViewModel.totalTime.observe(viewLifecycleOwner, Observer {
            if(it!= null)
                binding.totTimeText.text = "Total time: ${SimpleDateFormat("HH 'h' mm 'min' ss 'sec'").format(it)}"
            else
                binding.totTimeText.text = "Total time: ${0} milliseconds"
        })

        mainViewModel.totalCalories.observe(viewLifecycleOwner, Observer {
            if(it!= null)
                binding.totCalBurned.text = "Total calories: ${it} kcal"
            else
                binding.totCalBurned.text = "Total calories: ${0} kcal"
        })

        mainViewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            if(it!= null)
                binding.totAvgSpeed.text = "Average Speed: ${String.format("%.2f", it)} kmh"
            else
                binding.totAvgSpeed.text = "Average Speed: ${0.00f} kmh"
        })
    }
}