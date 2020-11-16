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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunshine.data.SunshinePreferences

class MainActivity : AppCompatActivity(),
        ForecastAdapter.ForecastAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        WeatherFeedsContract.WeatherFeedsView {

    private val mErrorMessageTextView: TextView by lazy { findViewById(R.id.tv_error_message) }
    private val mLoadingProgressBar: ProgressBar by lazy { findViewById(R.id.pb_loading) }
    private val mForecastRecyclerView: RecyclerView by lazy { findViewById(R.id.rv_forecast) }
    private lateinit var mAdapter: ForecastAdapter

    private val presenter = WeatherFeedsPresenter(this, WeatherFeedsModel())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mForecastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        mForecastRecyclerView.layoutManager = layoutManager

        mAdapter = ForecastAdapter(this@MainActivity)
        mForecastRecyclerView.adapter = mAdapter

        /* Register listener for changes of preferences*/
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                .registerOnSharedPreferenceChangeListener(this@MainActivity)

        presenter.loadWeather()
    }

    /* For create menu buttons*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                presenter.loadWeather()
                true
            }
            R.id.action_map -> {
                openLocationMap()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }/* Open settings page*/
    }

    /* Open DetailActivity to display weather details*/
    override fun onClick(weather: String?) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, weather)
        startActivity(intent)
    }


    /* Method for opening map app to display location*/
    private fun openLocationMap() {
        val location = SunshinePreferences.getPreferredWeatherLocation(this)
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


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        PREFERENCE_UPDATED = true
    }

    override fun onStart() {
        super.onStart()
        /* Restart Loader if preferences are updated*/
        if (PREFERENCE_UPDATED) {
            Log.d(TAG, "onStart: SharedPreferences have been updated.")
            presenter.loadWeather()
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
        private var PREFERENCE_UPDATED = false
    }

    override fun updateWeatherData(data: List<String>) {
        runOnUiThread {
            mErrorMessageTextView.visibility = View.GONE
            mLoadingProgressBar.visibility = View.GONE
            mForecastRecyclerView.visibility = View.VISIBLE
            mAdapter.setWeatherData(data.toTypedArray())
        }
    }

    override fun showErrorView(msg: String?) {
        runOnUiThread {
            mLoadingProgressBar.visibility = View.GONE
            mForecastRecyclerView.visibility = View.GONE
            mErrorMessageTextView.visibility = View.VISIBLE
            mErrorMessageTextView.text = msg
        }
    }

    override fun showLoading() {
        runOnUiThread {
            mErrorMessageTextView.visibility = View.GONE
            mForecastRecyclerView.visibility = View.GONE
            mLoadingProgressBar.visibility = View.VISIBLE
        }
    }
}
