package com.example.android.sunshine

import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.entity.WeatherForecastResp
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class WeatherFeedsModel : WeatherFeedsContract.WeatherFeedsModel {

    private val client: OkHttpClient = OkHttpClient()
    private val gson = Gson()

    override fun loadFromServer(callback: WeatherFeedsContract.WeatherFeedsModel.OnLoadResultCallback) {
        val location = SunshinePreferences.getPreferredWeatherLocation(SunshineApplication.APP_CONTEXT)
        val requestUrl = NetworkUtils.buildUrl(location)
        val request = Request.Builder().url(requestUrl).build()
        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onFailure(e.message)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val respStr = response.body?.string()
                        val data = gson.fromJson(respStr, WeatherForecastResp::class.java)
                        if (data == null || data.list.isEmpty()) {
                            callback.onFailure("no data. http code: ${response.message}")
                        } else {
                            callback.onSuccess(OpenWeatherJsonUtils.getSimpleWeatherStringsFromResp(data))
                        }
                    }
                })
    }
}