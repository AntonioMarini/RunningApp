package com.apollyon.samproject.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.apollyon.samproject.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this








        return binding.root
    }






}