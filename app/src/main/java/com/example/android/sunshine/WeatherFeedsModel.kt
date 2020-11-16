package com.example.android.sunshine

import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import okhttp3.*
import java.io.IOException

class WeatherFeedsModel : WeatherFeedsContract.WeatherFeedsModel {

    private val client: OkHttpClient = OkHttpClient()

    override fun loadFromServer(callback: WeatherFeedsContract.WeatherFeedsModel.OnLoadResultCallback) {
        val location = SunshinePreferences.getPreferredWeatherLocation(SunshineApplication.APP_CONTEXT)
        val requestUrl = NetworkUtils.buildUrl(location)
        val request = Request.Builder().url(requestUrl).build()
        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onFailure()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = OpenWeatherJsonUtils
                                .getSimpleWeatherStringsFromJson(SunshineApplication.APP_CONTEXT, response.body?.string())
                        if (data.isEmpty()) {
                            callback.onFailure()
                        } else {
                            callback.onSuccess(data.toList())
                        }
                    }
                })
    }
}