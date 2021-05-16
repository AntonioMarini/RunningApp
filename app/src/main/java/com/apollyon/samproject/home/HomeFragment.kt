package com.apollyon.samproject.home

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.apollyon.samproject.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentHomeBinding
import com.apollyon.samproject.datastruct.RunningDatabase
import com.apollyon.samproject.newsession.NewSessionFragment
import com.apollyon.samproject.profile.ProfileFragment
import com.apollyon.samproject.stats.StatsFragment
import com.apollyon.samproject.trainer.TrainerFragment
import com.bumptech.glide.Glide

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