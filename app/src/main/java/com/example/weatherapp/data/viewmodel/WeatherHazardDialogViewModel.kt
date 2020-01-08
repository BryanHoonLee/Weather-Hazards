package com.example.weatherapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherHazardDialogViewModel: ViewModel(){

    private val _loadUrl = MutableLiveData<Boolean>()
    val loadUrl: LiveData<Boolean> = _loadUrl

    fun loadUrlButtonClicked(){
        _loadUrl.value = true
    }

    fun loadUrlHandled(){
        _loadUrl.value = false
    }

}