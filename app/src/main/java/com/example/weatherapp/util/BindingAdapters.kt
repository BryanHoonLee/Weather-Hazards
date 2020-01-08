package com.example.weatherapp.util

import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.dialog_fragment_weather_hazard.*

// Usually Separate binding adapter files by the type of object you are working on

@BindingAdapter("zoomControls")
fun WebView.enableZoomControls(enable: Boolean){
    this.settings.builtInZoomControls = enable
}

@BindingAdapter("javaScript")
fun WebView.enableJavaScript(enable: Boolean){
    this.settings.javaScriptEnabled = enable
}

@BindingAdapter("visibility", "loadUrl")
fun WebView.loadUrl(visibility: Int, url: String){
    if(visibility == View.VISIBLE) {
        this.loadUrl(url)
    }
}

@BindingAdapter("viewpoint")
fun MapView.bindViewpoint(viewpoint: Viewpoint?){
    this.let{
        it.setViewpointAsync(viewpoint)
    }
}

@BindingAdapter("map")
fun MapView.bindMap(map: ArcGISMap?){
    this?.let {
        it.map = map
    }
}


