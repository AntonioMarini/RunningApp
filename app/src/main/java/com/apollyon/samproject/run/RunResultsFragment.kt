package com.apollyon.samproject.run

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRunResultsBinding
import com.apollyon.samproject.datastruct.RunningSession
import kotlinx.android.synthetic.main.fragment_run_results.*
import java.text.SimpleDateFormat
import java.util.*


class RunResultsFragment : Fragment(){

    private lateinit var binding: FragmentRunResultsBinding

    private lateinit var mapScreen : Bitmap
    private lateinit var session : RunningSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run_results, container, false)

        val arguments = RunResultsFragmentArgs.fromBundle(requireArguments())
        val rightNow: Calendar = Calendar.getInstance()

        session = arguments.session

        val km = session.distanceInMeters.toDouble() * 0.001
        binding.textKm.text = String.format("Distance: %.2f km", km)

        binding.textTime.text = getTimeElapsedFormattedString()


        binding.imgMap.setImageBitmap(arguments.mapScreenBitmap)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener{
            requireActivity().finish()
            this.findNavController().navigate(RunResultsFragmentDirections.actionRunResultsFragmentToMainActivity2(session))
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeElapsedFormattedString() : String{
        val elapsedTime = session.timeMilli
        return SimpleDateFormat("'Time:' HH'h':mm'm':ss's'").format(elapsedTime)
    }

}