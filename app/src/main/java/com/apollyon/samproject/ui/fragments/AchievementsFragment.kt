package com.apollyon.samproject.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentAchievementsBinding
import com.apollyon.samproject.ui.adapters.AchievementsAdapter
import com.apollyon.samproject.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class AchievementsFragment : Fragment() {

    val mainViewModel : MainViewModel by activityViewModels()

    private lateinit var binding : FragmentAchievementsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_achievements, container, false)

        // set the layout manager for the recycler view
        val linearLayManager = LinearLayoutManager(requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerAchievements.layoutManager = gridLayoutManager

        val adapter = AchievementsAdapter()
        binding.recyclerAchievements.adapter = adapter

        mainViewModel.allAchievements.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}