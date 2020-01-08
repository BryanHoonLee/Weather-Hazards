package com.example.weatherapp.data.network

import com.esri.arcgisruntime.mapping.ArcGISMap
import kotlin.math.log

// Retrieves data from multiple web services
class WebMapDataSource{
    private var webMapService: WebMapService

    init {
        webMapService = WebMapService()
    }

    fun getWebMap(id: String, url: String, loginRequired: Boolean): ArcGISMap{
        return webMapService.getWebMap(id, url, loginRequired)
    }
}