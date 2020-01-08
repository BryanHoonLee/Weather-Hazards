package com.example.weatherapp.data.model

data class Earthquake(
    val location: String,
    val magnitude: Float,
    val startDate: String,
    val url: String,
    val severityColor: Int,
    val tsunamiThreat: Boolean
)
