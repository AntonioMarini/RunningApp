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
import com.apollyon.samproject.databinding.FragmentMissionsBinding
import com.apollyon.samproject.ui.adapters.MissionsAdapter
import com.apollyon.samproject.viewmodels.MainViewModel

class MissionsFragment : Fragment() {

    val mainViewModel : MainViewModel by activityViewModels()

    private lateinit var binding : FragmentMissionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_missions, container, false)

        // set the layout manager for the recycler view
        val linearLayManager = LinearLayoutManager(requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(),2)  // no, brutto
        binding.recyclerMissions.layoutManager = linearLayManager

        val adapter = MissionsAdapter()
        binding.recyclerMissions.adapter = adapter

        mainViewModel.allMissions.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })

        return binding.root
    }
}