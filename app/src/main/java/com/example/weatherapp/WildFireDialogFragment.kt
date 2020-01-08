package com.example.weatherapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.navGraphViewModels
import com.example.weatherapp.data.viewmodel.SharedFeatureResultViewModel
import com.example.weatherapp.databinding.DialogFragmentWildfireBinding
import kotlinx.android.synthetic.main.dialog_fragment_wildfire.*
import kotlinx.android.synthetic.main.dialog_fragment_wildfire.text_view_active
import kotlinx.android.synthetic.main.dialog_fragment_wildfire.text_view_area
import kotlinx.android.synthetic.main.dialog_fragment_wildfire.text_view_start_date
import kotlinx.android.synthetic.main.dialog_fragment_wildfire.view.*
import java.text.SimpleDateFormat
import java.util.*

class WildFireDialogFragment: DialogFragment(){

    private val sharedFeatureResultViewModel: SharedFeatureResultViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Pinpoint.logFunnel("Wild Fire Dialog Fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogFragmentWildfireBinding.inflate(inflater, container, false)
        binding.sharedFeatureResultViewModel = sharedFeatureResultViewModel
        binding.lifecycleOwner = this

        sharedFeatureResultViewModel.wildFire.observe(requireActivity(), Observer {
            view.apply{
                dialog?.setTitle(it.name)
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        button_close.setOnClickListener {
            dialog?.dismiss()
        }

        button_load_url.setOnClickListener {
            sharedFeatureResultViewModel.loadUrlButtonClicked()
        }
    }


    override fun onStop() {
        sharedFeatureResultViewModel.loadUrlHandled()
        super.onStop()
    }
}