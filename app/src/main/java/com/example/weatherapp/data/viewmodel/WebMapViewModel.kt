package com.example.weatherapp.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.example.weatherapp.data.repository.WebMapRepository

class WebMapViewModel(val app: Application): AndroidViewModel(app){
    private var webMapRepository: WebMapRepository

    private var mLocationDisplay: LocationDisplay? = null

    private val _map = MutableLiveData<ArcGISMap>()
    val map: LiveData<ArcGISMap> = _map

    private val _viewPoint = MutableLiveData<Viewpoint>()
    val viewPoint: LiveData<Viewpoint> = _viewPoint

    init {
        webMapRepository = WebMapRepository()
        initWebMapFromArcGISOnline()
    }

    fun setViewPoint(viewpoint: Viewpoint){
        _viewPoint.value = viewpoint
    }

    fun initWebMapFromArcGISOnline(){
        Log.d("WEBMAPVIEWMODEL", map.value.toString())

        if (map.value == null) {
            val id = "e247c71c02e34c33aa498b7d857376bb"
            val url = "https://www.arcgis.com"
            val loginRequired = true
            val map = webMapRepository.getWebMap(id, url, loginRequired)
            map.initialViewpoint = Viewpoint(34.09042, -118.71511, 5000000.0)
            setViewPoint(map.initialViewpoint)
            _map.value = map
        }
        Log.d("WEBMAPVIEWMODEL", map.value.toString())

    }


    /**
     * Pans view of the Map View over to current location of user.
     */
     fun locateUser() {
        mLocationDisplay?.autoPanMode = LocationDisplay.AutoPanMode.COMPASS_NAVIGATION
        mLocationDisplay?.startAsync()
    }




}