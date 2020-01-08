package com.example.weatherapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.navGraphViewModels
import com.example.weatherapp.data.viewmodel.SharedFeatureResultViewModel
import com.example.weatherapp.data.viewmodel.WeatherHazardDialogViewModel
import com.example.weatherapp.databinding.DialogFragmentWeatherHazardBinding
import kotlinx.android.synthetic.main.dialog_fragment_weather_hazard.*
import kotlinx.android.synthetic.main.dialog_fragment_weather_hazard.view.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherHazardDialogFragment : DialogFragment() {

    private val sharedFeatureResultViewModel: SharedFeatureResultViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFunnel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogFragmentWeatherHazardBinding.inflate(inflater, container, false)
        binding.sharedFeatureResultViewModel = sharedFeatureResultViewModel
        binding.lifecycleOwner = this

        sharedFeatureResultViewModel.weatherHazard.observe(requireActivity(), Observer {
            dialog?.setTitle(it.name)
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //        webView.setOnKeyListener { v, keyCode, event ->
//            if (webView.isVisible && keyCode == KeyEvent.KEYCODE_BACK) {
//                webView.destroy()
//                weatherHazardDialogViewModel.loadUrlHandled()
//
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

    /** Sends Event Log for Funnel Analytics */
    private fun logFunnel() {
        val event = MainActivity.pinpointManager?.let {
            it.analyticsClient.createEvent("Result Bottom Sheet Fragment")
        }
        MainActivity.pinpointManager?.analyticsClient?.recordEvent(event)
    }

    override fun onStop() {
        sharedFeatureResultViewModel.loadUrlHandled()
        super.onStop()

    }

    //    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        activity?.run {
//            sharedFeatureResultViewModel = ViewModelProviders.of(this)[SharedFeatureResultViewModel::class.java]
//        }
//        var url = ""
//        val layoutInflater = activity!!.layoutInflater
//        val view = layoutInflater.inflate(R.layout.dialog_fragment_weather_hazard, null)
//        sharedFeatureResultViewModel.weatherHazard.observe(requireActivity(), Observer {
//            with(view){
//                text_view_name.text = it.name
//                text_view_location.text = it.location
//                text_view_start_date.text = "${convertMilliToDate(it.dateStart)} - "
//                text_view_end_date.text = convertMilliToDate(it.dateEnd)
//                text_view_summary.text = it.summary
//                url = it.url
//            }
//        })
//        val builder = AlertDialog.Builder(activity).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//            dialog?.dismiss()
//        }).setNeutralButton("See More...", DialogInterface.OnClickListener{ dialog, which ->
//            view.webView.loadUrl(url)
//            view.webView.visibility = View.VISIBLE
//        })
//
//        builder.setView(view)
//        return builder.create()
//    }
}