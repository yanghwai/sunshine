package com.example.android.sunshine

import android.app.Application
import android.content.Context

class SunshineApplication : Application() {
    companion object {
        lateinit var APP_CONTEXT: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = applicationContext
    }
}