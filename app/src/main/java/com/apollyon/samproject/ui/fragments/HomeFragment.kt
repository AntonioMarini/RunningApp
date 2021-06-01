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
import com.apollyon.samproject.databinding.FragmentHomeBinding
import com.apollyon.samproject.ui.adapters.SessionsAdapter
import kotlinx.android.synthetic.main.fragment_home.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.setHasFixedSize(true)

        // set the layout manager for the recycler view
        val linearLayManager: LinearLayoutManager = LinearLayoutManager(requireContext())
        recycler_view.layoutManager = linearLayManager

        //List<RunningSession>
        val sessionsAdapter = SessionsAdapter()
        recycler_view.adapter = sessionsAdapter

       mainViewModel.runSessions?.observe(viewLifecycleOwner, Observer { runs ->
            runs?.let {
                sessionsAdapter.data = it
            }
        })

        binding.clearButt.setOnClickListener {
            mainViewModel.clearSessions()
        }

        mainViewModel.totalkm.observe(viewLifecycleOwner, Observer {
            if(it!=null)
                binding.textTotalDist.text = it.toString() + "m"
        })
    }






}