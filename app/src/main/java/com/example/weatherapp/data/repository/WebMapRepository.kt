package com.example.weatherapp.data.repository

import com.esri.arcgisruntime.mapping.ArcGISMap
import com.example.weatherapp.data.network.WebMapDataSource
import com.example.weatherapp.data.network.WebMapService

class WebMapRepository{
    private var webMapDataService: WebMapDataSource

    init {
        webMapDataService = WebMapDataSource()
    }

    fun getWebMap(id: String, url: String, loginRequired: Boolean): ArcGISMap{
        return webMapDataService.getWebMap(id, url, loginRequired)
    }
}