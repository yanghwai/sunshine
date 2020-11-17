package com.example.android.sunshine.entity

data class WeatherForecastResp(val city: City, val cnt: Int, val list: List<WeatherForecast>)

data class Coordinate(
        val lon: Double,
        val lat: Double
)

data class City(
        val id: Int,
        val name: String,
        val coord: Coordinate,
        val country: String,
        val population: Long
)

data class WeatherForecast(
        val dt: Double,
        val temp: Temperature,
        val pressure: Double,
        val humidity: Int,
        val weather: List<WeatherBrief>,
        val speed: Double,
        val deg: Int,
        val clouds: Int
)

data class Temperature(
        val day: Double,
        val min: Double,
        val max: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
)

data class WeatherBrief(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)