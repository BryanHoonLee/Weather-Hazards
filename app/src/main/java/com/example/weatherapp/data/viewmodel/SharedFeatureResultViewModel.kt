package com.example.weatherapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esri.arcgisruntime.data.Feature
import com.example.weatherapp.data.model.Earthquake
import com.example.weatherapp.data.model.WeatherHazard
import com.example.weatherapp.data.model.Wildfire
import java.text.SimpleDateFormat
import java.util.*

class SharedFeatureResultViewModel: ViewModel(){
    private val _sharedFeatureResult = MutableLiveData<List<Feature>>()
    val sharedFeatureResult: LiveData<List<Feature>> = _sharedFeatureResult

    private val _weatherHazard = MutableLiveData<WeatherHazard>()
    val weatherHazard = _weatherHazard

    private val _wildfire = MutableLiveData<Wildfire>()
    val wildFire = _wildfire

    private val _earthquake = MutableLiveData<Earthquake>()
    val earthquake = _earthquake

    private val _loadUrl = MutableLiveData<Boolean>()
    val loadUrl: LiveData<Boolean> = _loadUrl

    fun setWildfire(wildfire: Wildfire){
        _wildfire.value = wildfire
    }

    fun setEarthquake(earthquake: Earthquake){
        _earthquake.value = earthquake

    }

    fun setWeatherHazard(weatherHazard: WeatherHazard){
        _weatherHazard.value = weatherHazard
    }

    fun setSharedFeatureResult(featureResult: List<Feature>){
        _sharedFeatureResult.value = featureResult
    }


    fun loadUrlButtonClicked(){
        _loadUrl.value = true
    }

    fun loadUrlHandled(){
        _loadUrl.value = false
    }

    fun convertMilliToDate(gregorian: String): String{
        var split = gregorian.split("\\[|,".toRegex())
        var millisecond = split[1].substring(5).toLong()
        var date = Date(millisecond)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }

}