package com.example.android.sunshine

interface WeatherFeedsContract {
    interface WeatherFeedsView {
        fun updateWeatherData(data: List<String>)
        fun showErrorView(msg: String?)
        fun showLoading()
    }

    interface WeatherFeedsModel {
        interface OnLoadResultCallback {
            fun onSuccess(data: List<String>)
            fun onFailure(reason: String?)
        }

        fun loadFromServer(callback: OnLoadResultCallback)
    }
}