package com.apollyon.samproject.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.apollyon.samproject.MainViewModel
import com.apollyon.samproject.R

class HomeFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val welcomeText : TextView = view.findViewById(R.id.textView_welcome)

        mainViewModel.user.observe(viewLifecycleOwner, Observer {user ->
            welcomeText.text = user.username
        })

    }

    companion object {
        @JvmStatic
        fun newInstance() =
                HomeFragment().apply {
                }
    }
}