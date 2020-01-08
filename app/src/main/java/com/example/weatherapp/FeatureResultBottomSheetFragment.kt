package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.FeatureResultAdapter
import com.example.weatherapp.data.model.Earthquake
import com.example.weatherapp.data.model.WeatherHazard
import com.example.weatherapp.data.model.Wildfire
import com.example.weatherapp.data.viewmodel.SharedFeatureResultViewModel
import com.example.weatherapp.databinding.BottomSheetDialogFragmentFeatureResultBinding
import com.example.weatherapp.util.MarginItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FeatureResultBottomSheetFragment : BottomSheetDialogFragment(),
    FeatureResultAdapter.OnFeatureResultItemClickListener {

    private val sharedFeatureResultViewModel: SharedFeatureResultViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeatureResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFunnel()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomSheetDialogFragmentFeatureResultBinding.inflate(inflater, container, false)
        sharedFeatureResultViewModel.sharedFeatureResult.observe(this, Observer { featureResult ->
            adapter.setFeatureList(featureResult)
        })
        initRecyclerView(binding.root)

        return binding.root
    }

    override fun onFeatureResultItemClick(position: Int) {
        val featureType = adapter.getItemViewType(position)
        val current = adapter.getFeatureList()[position]
        val currentAttribute = current.attributes
        val currentField = current.featureTable.fields
        when (featureType) {

            FeatureResultAdapter.TYPE_WEATHER_HAZARD -> {
                var severity = currentAttribute[currentField[2].name].toString()
                var color = adapter.getSeverityColor(severity, requireContext())

                val weatherHazard = WeatherHazard(
                    currentAttribute[currentField[1].name].toString(),
                    currentAttribute[currentField[12].name].toString(),
                    currentAttribute[currentField[9].name].toString(),
                    currentAttribute[currentField[10].name].toString(),
                    currentAttribute[currentField[3].name].toString(),
                    currentAttribute[currentField[4].name].toString(),
                    color)
                sharedFeatureResultViewModel.setWeatherHazard(weatherHazard)
                navigateToWeatherHazardDialogFragment()
            }

            FeatureResultAdapter.TYPE_WILDFIRE -> {
                var active = currentAttribute[currentField[11].name].toString().equals("Y")

                val wildFire = Wildfire(
                    "${currentAttribute[currentField[4].name].toString()} Fire",
                    currentAttribute[currentField[3].name].toString(),
                    currentAttribute[currentField[18].name].toString().toDouble(),
                    currentAttribute[currentField[15].name].toString(),
                    currentAttribute[currentField[22].name].toString(),
                    active
                    )
                sharedFeatureResultViewModel.setWildfire(wildFire)
                navigateToWildFireDialogFragment()
            }

            FeatureResultAdapter.TYPE_EARTHQUAKE -> {
                val tsunamiThreat = (currentAttribute[currentField[16].name] != null)
                val severityColor = adapter.getSeverityColor(currentAttribute[currentField[5].name].toString(), requireContext())

                val earthquake = Earthquake(
                    currentAttribute[currentField[6].name].toString(),
                    currentAttribute[currentField[3].name] as Float,
                    currentAttribute[currentField[32].name].toString(),
                    currentAttribute[currentField[10].name].toString(),
                    severityColor,
                    tsunamiThreat
                    )
                sharedFeatureResultViewModel.setEarthquake(earthquake)
                navigateToEarthquakeDialogFragment()
            }
        }
    }

    /** Sends Event Log for Funnel Analytics */
    private fun logFunnel() {
        val event = MainActivity.pinpointManager?.let {
            it.analyticsClient.createEvent("Result Bottom Sheet Fragment")
        }
        MainActivity.pinpointManager?.analyticsClient?.recordEvent(event)
    }

    private fun navigateToWeatherHazardDialogFragment(){
        if(findNavController().currentDestination?.id != R.id.weatherHazardDialogFragment) {
            findNavController().navigate(R.id.weatherHazardDialogFragment)
        }
    }

    private fun navigateToWildFireDialogFragment(){
        if(findNavController().currentDestination?.id != R.id.wildFireDialogFragment) {
            findNavController().navigate(R.id.wildFireDialogFragment)
        }
    }

    private fun navigateToEarthquakeDialogFragment(){
        if(findNavController().currentDestination?.id != R.id.earthquakeDialogFragment) {
            findNavController().navigate(R.id.earthquakeDialogFragment)
        }
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        adapter = FeatureResultAdapter(this)
        recyclerView.adapter = adapter
    }
}