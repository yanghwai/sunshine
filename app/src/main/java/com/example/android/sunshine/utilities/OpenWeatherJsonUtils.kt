package com.example.android.sunshine.utilities

import com.example.android.sunshine.SunshineApplication
import com.example.android.sunshine.entity.WeatherForecastResp

object OpenWeatherJsonUtils {

    fun getSimpleWeatherStringsFromResp(resp: WeatherForecastResp): List<String> {
        val localDate = System.currentTimeMillis()
        val utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate)
        val startDay = SunshineDateUtils.normalizeDate(utcDate)
        return resp.list.mapIndexed { i, forecast ->
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
}