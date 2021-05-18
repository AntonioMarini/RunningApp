package com.apollyon.samproject.run

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apollyon.samproject.R
import com.apollyon.samproject.databinding.FragmentRunMapBinding
import com.apollyon.samproject.databinding.FragmentRunResultsBinding
import kotlinx.android.synthetic.main.fragment_run_results.*


class RunResultsFragment : Fragment(){

    private lateinit var binding: FragmentRunResultsBinding

    private lateinit var mapScreen : Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run_results, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener{
            requireActivity().finish()
            this.findNavController().navigate(RunResultsFragmentDirections.actionRunResultsFragmentToMainActivity2())
        }

    }

}