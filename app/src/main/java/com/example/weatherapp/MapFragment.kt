package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.weatherapp.data.viewmodel.SharedFeatureResultViewModel
import com.example.weatherapp.data.viewmodel.WebMapViewModel
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.util.await
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class MapFragment : Fragment() {

    private val webMapViewModel: WebMapViewModel by viewModels()
    private val sharedFeatureResultViewModel: SharedFeatureResultViewModel by activityViewModels()

    private var mLocationDisplay: LocationDisplay? = null
    private lateinit var mapView: MapView

    //    private var identifyFuture: ListenableFuture<List<IdentifyLayerResult>>? = null
    private var identifyJob: Job? = null

    private val featureList = mutableListOf<Feature>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFunnel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapviewmodel = webMapViewModel
        binding.lifecycleOwner = this

        mapView = binding.mapView
        mapView.onTouchListener = createDisplayFeatureListener()

        mLocationDisplay = mapView.locationDisplay

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        fab.setOnClickListener {
            checkLocationPerm()
        }

        button_display_feature_bottom_sheet.setOnClickListener {
            navigateToBottomSheetDialogFragment()
        }
    }


    /**
     * Returns a custom [DefaultMapViewOnTouchListener] that grabs every Feature from every Layer
     * from the point touched on the Map View.
     * Starts a Bottom Sheet Dialog Fragment that holds each Feature.
     */
    private fun createDisplayFeatureListener() =
        object : DefaultMapViewOnTouchListener(this.context, mapView) {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                e ?: return false
                val screenPoint = Point(e.x.toInt(), e.y.toInt())
                displayFeatureList(screenPoint)
                logMapClicked()

                return true
            }
        }

    private fun displayFeatureList(screenPoint: Point) {

        identifyJob?.cancel()
        identifyJob = CoroutineScope(Dispatchers.Main).launch {
            val identifyFuture = mapView.identifyLayersAsync(screenPoint, 15.0, false, 25).await()

            identifyFuture?.let {

                try {
                    val identifyLayerResults = it

                    featureList.clear()

                    logMapLayerSize(identifyLayerResults.size)

                    iterateLayerResults(identifyLayerResults)

//                    val duplicateRemovedFeatureList = featureList.distinctBy {
//                        if (it.featureTable.tableName.equals("shake intensity", true)) {
//                            it.attributes[it.featureTable.fields[3].name]
//                        }
//                    }

                    sharedFeatureResultViewModel.setSharedFeatureResult(featureList)

                    navigateToBottomSheetDialogFragment()

                    Log.i("List Size", "${featureList.size}")

                } catch (e: InterruptedException) {
                    Log.d("DisplayFeatureListener", "Interrupted Exception")
                } catch (e: ExecutionException) {
                    Log.d("DisplayFeatureListener", "Execution Exception")
                }
            }

        }
    }

    /**
     * Iterates through each layer and finds every Feature and puts them into featureList.
     */
    private fun iterateLayerResults(identifyLayerResults: List<IdentifyLayerResult>) {
        if ((identifyLayerResults == null) || (identifyLayerResults.size < 1)) return

        identifyLayerResults.forEach { identifyLayerResult ->
            featureList.addAll(identifyLayerResult.elements.filterIsInstance<Feature>())
            iterateLayerResults(identifyLayerResult.sublayerResults)
        }

        // union all geometries and pan here
        if (featureList.size > 0) {
            panCameraToCenterOfGeometry(
                featureList[0].geometry,
                200000
            )
        }
    }

    /**
     * Creates a Bottom Sheet Dialog Fragment over the Map View.
     */
    private fun navigateToBottomSheetDialogFragment() {
        if (featureList.size > 0) {
            if (findNavController().currentDestination?.id != R.id.featureResultBottomSheetFragment) {
                findNavController().navigate(R.id.featureResultBottomSheetFragment)
            }
        } else {
            Snackbar.make(this.requireView(), "No Features to display.", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    /** Pans camera to center of [geometry] if Map Scale is greater than [scaleGreaterThan]*/
    private fun panCameraToCenterOfGeometry(geometry: Geometry, scaleGreaterThan: Int) {
        val geometryMaxScale = maxOf(geometry.extent.xMax, geometry.extent.yMax)
        if (mapView.mapScale > scaleGreaterThan) {
            if (mapView.mapScale < geometryMaxScale) {
                mapView.setViewpointGeometryAsync(geometry, geometryMaxScale)
            } else {
                mapView.setViewpointCenterAsync(geometry.extent.center)
            }
        }

    }

    /** Sends Event Log to AWS Pinpoint when [map] is clicked. */
    private fun logMapClicked() {
        val event = MainActivity.pinpointManager?.let {
            it.analyticsClient.createEvent("Map Clicked")
                .withAttribute("TEST1", "TEST1")
                .withMetric("TESTMETRIC", Math.random())
        }
        Log.i("logEventMapClicked", "${event.toString()}")
        MainActivity.pinpointManager?.analyticsClient?.recordEvent(event)
    }

    /** Sends Event Log with # of Map Layers to AWS Pinpoint when [map] is clicked. */
    private fun logMapLayerSize(numOfLayers: Int) {
        val event = MainActivity.pinpointManager?.let {
            it.analyticsClient.createEvent("Map Layer Size")
                .withAttribute("Number Of Layers", numOfLayers.toString())
        }
        Log.i("logEventMapLayerSize", "$numOfLayers")
        MainActivity.pinpointManager?.analyticsClient?.recordEvent(event)

    }

    /** Sends Event Log for Funnel Analytics */
    private fun logFunnel() {
        val event = MainActivity.pinpointManager?.let {
            it.analyticsClient.createEvent("Map Fragment")
        }
        MainActivity.pinpointManager?.analyticsClient?.recordEvent(event)
    }

    /**
     * Pans view of the Map View over to current location of the user and displays feature list.
     */
    private fun locateUser() {
        mLocationDisplay?.let { locationDisplay ->
            locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.COMPASS_NAVIGATION

            lateinit var listener: LocationDisplay.LocationChangedListener

            listener = LocationDisplay.LocationChangedListener { event ->
                locationDisplay.removeLocationChangedListener(listener)

                val point = event.location.position
                val convertedPoint = mapView.locationToScreen(point)
                val screenPoint = Point(convertedPoint)

                displayFeatureList(screenPoint)
            }

            locationDisplay.addLocationChangedListener(listener)

            locationDisplay.startAsync()
        }
    }

    /**
     * Checks if user has location permissions.
     * If they don't, then it requests permissions.
     * If they do, then it calls the function [locateUser]
     */
    private fun checkLocationPerm() {
        val requestPermissionsCode = 2
        val requestPermissions = arrayOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (!(checkSelfPermission(
                this.requireContext(),
                requestPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                this.requireContext(),
                requestPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(
                requestPermissions,
                requestPermissionsCode
            )
        } else {
            locateUser()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locateUser()
        } else {
            Toast.makeText(
                this.context,
                "User denied perms for device location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        mapView.let {
            it.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mapView.let {
            it.resume()
        }
    }

    override fun onDestroy() {
        mapView.let {
            webMapViewModel.setViewPoint(it.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE))
            it.dispose()
        }
        super.onDestroy()
    }
}