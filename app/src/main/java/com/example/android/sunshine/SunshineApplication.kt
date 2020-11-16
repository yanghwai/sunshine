package com.example.android.sunshine

import android.app.Application
import android.content.Context

class SunshineApplication : Application() {
    companion object {
        var APP_CONTEXT: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = applicationContext
    }
}