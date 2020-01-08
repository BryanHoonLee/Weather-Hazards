package com.example.weatherapp.data.model

data class WeatherHazard(
    val name: String,
    val location: String,
    val dateStart: String,
    val dateEnd: String,
    val summary: String,
    val url: String,
    val severityColor: Int
)