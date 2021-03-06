package com.apollyon.samproject.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollyon.samproject.viewmodels.MainViewModel
import com.apollyon.samproject.R
import com.apollyon.samproject.data.Mission
import com.apollyon.samproject.databinding.FragmentHomeBinding
import com.apollyon.samproject.ui.adapters.SessionsAdapter
import com.apollyon.samproject.utilities.RunUtil
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.onShowBars()
        recycler_view.setHasFixedSize(true)

        // set the layout manager for the recycler view
        val linearLayManager = LinearLayoutManager(requireContext())
        recycler_view.layoutManager = linearLayManager

        //List<RunningSession>
        val sessionsAdapter = SessionsAdapter()
        recycler_view.adapter = sessionsAdapter

        mainViewModel.allUserSessions.observe(viewLifecycleOwner, Observer { runs ->
            runs?.let {
                sessionsAdapter.data = it
            }
        })

        binding.clearButt.setOnClickListener {
            mainViewModel.clearSessions()
        }

        mainViewModel.totalKm.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                binding.textTotalDist.text = String.format("%.2f km", RunUtil.getDistanceKm(it))
                mainViewModel.onRunFinished(it)
            }
            else{
                binding.textTotalDist.text = String.format("%.2f km", 0.00f)
            }
        })
    }
}