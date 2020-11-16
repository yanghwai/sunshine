package com.example.android.sunshine

class WeatherFeedsPresenter(private val view: WeatherFeedsContract.WeatherFeedsView,
                            private val model: WeatherFeedsContract.WeatherFeedsModel) : WeatherFeedsContract.WeatherFeedsModel.OnLoadResultCallback {
    fun loadWeather() {
        view.showLoading()
        model.loadFromServer(this)
    }

    override fun onSuccess(data: List<String>) {
        view.updateWeatherData(data)
    }

    override fun onFailure(reason: String?) {
        view.showErrorView(reason)
    }

}