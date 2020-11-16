package com.example.android.sunshine

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunshine.data.SunshinePreferences
import com.example.android.sunshine.data.WeatherViewModel
import com.example.android.sunshine.utilities.NetworkUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import org.json.JSONException
import java.io.IOException

class MainActivity : AppCompatActivity(),
        ForecastAdapter.ForecastAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Array<String>> {

    private lateinit var mErrorMessageTextView: TextView
    private lateinit var mLoadingProgressBar: ProgressBar
    private lateinit var mForecastRecyclerView: RecyclerView
    private val model by lazy {
        WeatherViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mErrorMessageTextView = findViewById(R.id.tv_error_message)
        mLoadingProgressBar = findViewById(R.id.pb_loading)

        mForecastRecyclerView = findViewById(R.id.rv_forecast)
        mForecastRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        mForecastRecyclerView.layoutManager = layoutManager

        val adapter = ForecastAdapter(this@MainActivity)
        mForecastRecyclerView.adapter = adapter

        /* Register listener for changes of preferences*/
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                .registerOnSharedPreferenceChangeListener(this@MainActivity)
        LoaderManager.getInstance(this)
                .initLoader(FORECAST_ASYNC_LOADER_ID, null, this@MainActivity)
        model.weatherData.observe(this) {
            adapter.setWeatherData(it.toTypedArray())
        }
    }

    /* For create menu buttons*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                model.weatherData.value = null
                reloadWeatherData()
                return true
            }
            R.id.action_map -> {
                openLocationMap()
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }/* Open settings page*/
    }

    private fun showWeatherDataView() {
        mErrorMessageTextView.visibility = View.INVISIBLE
        mForecastRecyclerView.visibility = View.VISIBLE
    }

    private fun showErrorMessageView() {
        mErrorMessageTextView.visibility = View.VISIBLE
        mForecastRecyclerView.visibility = View.INVISIBLE
    }

    /* Restart asyncloader to refresh weather data*/
    private fun reloadWeatherData() {
        showWeatherDataView()
        LoaderManager.getInstance(this)
                .restartLoader(FORECAST_ASYNC_LOADER_ID, null, this@MainActivity)
    }


    /* Open DetailActivity to display weather details*/
    override fun onClick(weather: String?) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, weather)
        startActivity(intent)
    }


    /* Method for opening map app to display location*/
    private fun openLocationMap() {
        val location = SunshinePreferences.getPreferredWeatherLocation(this@MainActivity)
        val builder = Uri.Builder()
        val uri = builder.scheme("geo")
                .path("0,0")
                .appendQueryParameter("q", location)
                .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d(TAG, "openLocationMap: cannot open $uri . No receiving apps.")
        }

    }


    /* Use AsyncTaskLoader to asynchronously fetch weather data*/
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Array<String>> {
        return object : AsyncTaskLoader<Array<String>>(this@MainActivity) {

            private var mForecastData: Array<String>? = null // Used to store loaded forecast

            override fun onStartLoading() {
                /* If forecast data already exists, just deliver the result*/
                if (mForecastData != null)
                    deliverResult(mForecastData)
                else {
                    mLoadingProgressBar.visibility = View.VISIBLE
                    forceLoad()
                }
            }

            override fun loadInBackground(): Array<String>? {
                val location = SunshinePreferences.getPreferredWeatherLocation(this@MainActivity)
                val requestUrl = NetworkUtils.buildUrl(location)

                return try {
                    val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl!!)
                    OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(this@MainActivity, jsonWeatherResponse)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                } catch (e: JSONException) {
                    e.printStackTrace()
                    null
                }

            }

            override fun deliverResult(data: Array<String>?) {
                mForecastData = data
                super.deliverResult(data)
            }
        }
    }

    /* After loading is finished*/
    override fun onLoadFinished(loader: Loader<Array<String>>, data: Array<String>?) {
        /* Make loading bar invisible*/
        mLoadingProgressBar.visibility = View.INVISIBLE
        if (data != null) {
            showWeatherDataView()
            model.weatherData.value = data.toList()
        } else {
            showErrorMessageView()
        }
    }

    override fun onLoaderReset(loader: Loader<Array<String>>) {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        PREFERENCE_UPDATED = true
    }

    override fun onStart() {
        super.onStart()
        /* Restart Loader if preferences are updated*/
        if (PREFERENCE_UPDATED) {
            Log.d(TAG, "onStart: SharedPreferences have been updated.")
            LoaderManager.getInstance(this)
                    .restartLoader(FORECAST_ASYNC_LOADER_ID, null, this@MainActivity)
            PREFERENCE_UPDATED = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                .unregisterOnSharedPreferenceChangeListener(this@MainActivity)
    }

    companion object {

        private const val TAG = "MainActivity"
        private const val FORECAST_ASYNC_LOADER_ID = 810
        private var PREFERENCE_UPDATED = false
    }
}
