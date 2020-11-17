package com.example.android.sunshine.entity

import com.example.android.sunshine.SunshineApplication
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils

data class WeatherForecastResp(
        val city: City,
        val cnt: Int,
        val list: List<WeatherForecast>
)

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

/**
 * Convert [WeatherForecastResp] to human-friendly weather briefs
 * @return list of weather briefs for the next n days
 * */
fun WeatherForecastResp.toSimpleStrings(): List<String> {
    val localDate = System.currentTimeMillis()
    val utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate)
    val startDay = SunshineDateUtils.normalizeDate(utcDate)
    return this.list.mapIndexed { i, forecast ->
        val dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i
        val date = SunshineDateUtils.getFriendlyDateString(SunshineApplication.APP_CONTEXT, dateTimeMillis, false)
        /*
         * Description is in a child array called "weather", which is 1 element long.
         * That element also contains a weather code.
         */
        val desc = forecast.weather[0].main
        val highAndLow = SunshineWeatherUtils.formatHighLows(SunshineApplication.APP_CONTEXT, forecast.temp.max, forecast.temp.min)
        "$date - $desc - $highAndLow"
    }
}