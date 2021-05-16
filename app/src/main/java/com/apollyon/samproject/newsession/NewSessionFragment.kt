package com.apollyon.samproject.newsession

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.apollyon.samproject.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.UI.Adapter
import com.apollyon.samproject.UI.TrainingMode
import com.apollyon.samproject.databinding.FragmentNewSessionBinding
import com.apollyon.samproject.datastruct.FetchRunningSession


class NewSessionFragment : Fragment(), ViewPager.OnPageChangeListener{

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: Adapter

    private val mainViewModel : MainViewModel by activityViewModels()

    private lateinit var binding : FragmentNewSessionBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_session, container, false)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        val sessionViewModel = ViewModelProvider(this, SessionViewModelFactory(mainViewModel.database, requireActivity().application)).get(SessionViewModel::class.java)
        binding.sessionViewModel = sessionViewModel

        val trainingModes = ArrayList<TrainingMode>()
        trainingModes.add(TrainingMode(R.drawable.raccoon, "Standard Running", "This is pretty normal"))
        trainingModes.add(TrainingMode(R.drawable.ghepardo, "HIIT Running", "High-intensity interval training running consists of intervals of high intensive run with lower intensive intervals"))
        trainingModes.add(TrainingMode(R.drawable.falco, "Cycling", "Bike is nice"))

        adapter = Adapter(trainingModes, context)

        viewPager = binding.pager
        viewPager.adapter = adapter
        viewPager.setPadding(130, 0, 130, 0)

        binding.buttonStart.setOnClickListener {
            this.findNavController().navigate(NewSessionFragmentDirections.actionNewSessionToRunActivity())
        }

        viewPager.addOnPageChangeListener(this)


        return binding.root
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            ;
    }

    override fun onPageSelected(position: Int) {
            ;
    }

    override fun onPageScrollStateChanged(state: Int) {
            ;
    }





}