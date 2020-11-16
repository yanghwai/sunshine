package com.example.android.sunshine.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel: ViewModel() {
    val weatherData: MutableLiveData<Collection<String>> by lazy {
        MutableLiveData<Collection<String>>()
    }
}