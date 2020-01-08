package com.example.weatherapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.navGraphViewModels
import com.example.weatherapp.data.viewmodel.SharedFeatureResultViewModel
import com.example.weatherapp.databinding.DialogFragmentEarthquakeBinding
import kotlinx.android.synthetic.main.dialog_fragment_earthquake.*
import kotlinx.android.synthetic.main.dialog_fragment_earthquake.view.*
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeDialogFragment: DialogFragment(){

    private val sharedFeatureResultViewModel: SharedFeatureResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogFragmentEarthquakeBinding.inflate(inflater, container, false)
        binding.sharedFeatureResultViewModel = sharedFeatureResultViewModel
        binding.lifecycleOwner = this

        sharedFeatureResultViewModel.earthquake.observe(requireActivity(), Observer{
            view.apply{
                dialog?.setTitle("${it.magnitude} Earthquake")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        webView.setOnKeyListener { v, keyCode, event ->
//            if (webView.isVisible && keyCode == KeyEvent.KEYCODE_BACK){
//                webView.destroy()
//                webView.visibility = View.GONE
//                linearLayout1.visibility = View.VISIBLE
//            }
//            true
//        }

        button_close.setOnClickListener {
            dialog?.dismiss()
        }

        button_load_url.setOnClickListener {
            sharedFeatureResultViewModel.loadUrlButtonClicked()
        }
    }

    override fun onDestroy() {
        sharedFeatureResultViewModel.loadUrlHandled()
        super.onDestroy()
    }
}