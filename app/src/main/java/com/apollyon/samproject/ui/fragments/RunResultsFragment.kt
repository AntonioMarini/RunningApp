package com.apollyon.samproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRunResultsBinding
import com.apollyon.samproject.data.RunningSession
import com.apollyon.samproject.ui.ProgressAnimation
import com.apollyon.samproject.utilities.LevelUtil
import com.apollyon.samproject.utilities.RunUtil
import com.apollyon.samproject.viewmodels.MainViewModel

class RunResultsFragment : Fragment(){


    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding: FragmentRunResultsBinding
    private lateinit var session : RunningSession


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run_results, container, false)

        val arguments = RunResultsFragmentArgs.fromBundle(requireArguments()) // prendo la sessione dal bundle degli argomenti (safeargs)
        session = arguments.session!!

        val km = session.distanceInMeters.toDouble() * 0.001
        val xpGained = LevelUtil.calculateXpFromRun(session.caloriesBurned, session.avgSpeedInKMH)

        binding.xpGained.text = "XP gained from run: ${xpGained}xp"
        binding.textKm.text = String.format("Distance: %.2f km", km)
        binding.textTime.text = "Time: ${RunUtil.getFormattedTime(session.timeMilli)}"
        binding.textCalories.text = "Calories burned: ${session.caloriesBurned}"
        binding.imgMap.setImageBitmap(arguments.mapScreenBitmap)

        val maxLevelXp = LevelUtil.xpForNextLevel(mainViewModel.userFromRealtime.value!!.level!! - 1)
        val currentXp = maxLevelXp - mainViewModel.userFromRealtime.value!!.xpToNextLevel!!

        binding.progressBar2.max = maxLevelXp.toInt()
        val progressAnimation = ProgressAnimation(binding.progressBar2, 0, currentXp.toInt())
        progressAnimation.duration = 1000
        binding.progressBar2.startAnimation(progressAnimation)

        session.map_screen = arguments.mapScreenBitmap

        getXp(currentXp, xpGained)

        return binding.root
    }

    fun getXp(currentXp:Long, xpGained: Long){
        val oldLevel = mainViewModel.userFromRealtime.value!!.level!!
        val oldXp = currentXp

        mainViewModel.addXp(xpGained)

        val newLevel = mainViewModel.userFromRealtime.value!!.level!!
        val newXp = LevelUtil.xpForNextLevel(mainViewModel.userFromRealtime.value!!.level!! - 1) - mainViewModel.userFromRealtime.value!!.xpToNextLevel!!

        animateProgressBar(oldLevel, oldXp, newLevel, newXp)
    }


    fun animateProgressBar(oldLevel: Int, oldXp:Long, newLevel:Int, newXp: Long){
        var currLevel = oldLevel
        var maxLevelXp = LevelUtil.xpForNextLevel(currLevel-1)
        var progressAnimation : ProgressAnimation

        while(currLevel < newLevel){
            progressAnimation = ProgressAnimation(binding.progressBar2, 0, maxLevelXp.toInt())
            progressAnimation.duration = 1000


            currLevel++
            // 1: riempi tutta la progressbar
            // 2:ottieni nuovo max
            binding.progressBar2.startAnimation(progressAnimation)

            maxLevelXp = LevelUtil.xpForNextLevel(currLevel-1)
            binding.progressBar2.max = maxLevelXp.toInt()
            binding.progressBar2.progress = 0
        }
        // 3:setta progress = newXp
        binding.progressBar2.setProgress(newXp.toInt())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener{
            mainViewModel.insertSession(session)
            this.findNavController().navigate(RunResultsFragmentDirections.actionRunResultsFragmentToHome())
        }
    }



}