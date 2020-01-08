package com.example.weatherapp.data.model

data class Wildfire(
    val name: String,
    val areaType: String,
    val area: Double,
    val url: String,
    val startDate: String,
    val active: Boolean
)