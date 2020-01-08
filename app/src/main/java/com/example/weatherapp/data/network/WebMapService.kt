package com.example.weatherapp.data.network

import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.security.Credential
import com.esri.arcgisruntime.security.UserCredential

// Provides web maps from ArcGISOnline
class WebMapService(){

    fun getWebMap(id: String, url: String, loginRequired: Boolean): ArcGISMap{
        val portal = Portal(url, loginRequired)
        portal.credential = UserCredential("","")
        val portalItem = PortalItem(portal, id)
        val map = ArcGISMap(portalItem)

        return map
    }
}