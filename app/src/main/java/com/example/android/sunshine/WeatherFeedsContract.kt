package com.example.android.sunshine

interface WeatherFeedsContract {
    interface WeatherFeedsView {
        fun updateWeatherData(data: List<String>)
        fun showErrorView()
        fun showLoading()
    }

    interface WeatherFeedsModel {
        interface OnLoadResultCallback {
            fun onSuccess(data: List<String>)
            fun onFailure()
        }

        fun loadFromServer(callback: OnLoadResultCallback)
    }
}