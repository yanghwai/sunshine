package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FORECAST_ASYNC_LOADER_ID = 810;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;
    private RecyclerView mForecastRecyclerView;
    private static boolean PREFERENCE_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mLoadingProgressBar = findViewById(R.id.pb_loading);

        mForecastRecyclerView = findViewById(R.id.rv_forecast);
        mForecastRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        mForecastRecyclerView.setLayoutManager(layoutManager);

        mForecastAdapter = new ForecastAdapter(MainActivity.this);
        mForecastRecyclerView.setAdapter(mForecastAdapter);

        /* Register listener for changes of preferences*/
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .registerOnSharedPreferenceChangeListener(MainActivity.this);
        getSupportLoaderManager().initLoader(FORECAST_ASYNC_LOADER_ID, null, MainActivity.this);
    }

    /* For create menu buttons*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int clickedId = item.getItemId();
        if(clickedId == R.id.action_refresh){
            mForecastAdapter.setWeatherData(null);
            reloadWeatherData();
            return true;
        }
        else if(clickedId == R.id.action_map){
            openLocationMap();
            return true;
        }
        /* Open settings page*/
        else if (clickedId == R.id.action_settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void showWeatherDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mForecastRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessageView(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mForecastRecyclerView.setVisibility(View.INVISIBLE);
    }

    /* Restart asyncloader to refresh weather data*/
    private void reloadWeatherData() {
        showWeatherDataView();
        getSupportLoaderManager().restartLoader(FORECAST_ASYNC_LOADER_ID, null, MainActivity.this);
    }


    /* Open DetailActivity to display weather details*/
    @Override
    public void onClick(String weather) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weather);
        startActivity(intent);
    }


    /* Method for opening map app to display location*/
    public void openLocationMap(){
        String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("geo")
                .path("0,0")
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else{
            Log.d(TAG, "openLocationMap: cannot open " + uri.toString() + " . No receiving apps.");
        }

    }


    /* Use AsyncTaskLoader to asynchronously fetch weather data*/
    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[]>(MainActivity.this) {

            private String[] mForecastData; // Used to store loaded forecast

            @Override
            protected void onStartLoading() {
                /* If forecast data already exists, just deliver the result*/
                if(mForecastData != null)
                    deliverResult(mForecastData);
                else {
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL requestUrl = NetworkUtils.buildUrl(location);

                try {
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                    return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mForecastData = data;
                super.deliverResult(data);
            }
        };
    }

    /* After loading is finished*/
    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        /* Make loading bar invisible*/
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if(data != null){
            showWeatherDataView();
            mForecastAdapter.setWeatherData(data);
        }else{
            showErrorMessageView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCE_UPDATED = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        /* Restart Loader if preferences are updated*/
        if (PREFERENCE_UPDATED){
            Log.d(TAG, "onStart: SharedPreferences have been updated.");
            getSupportLoaderManager().restartLoader(FORECAST_ASYNC_LOADER_ID, null, MainActivity.this);
            PREFERENCE_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .unregisterOnSharedPreferenceChangeListener(MainActivity.this);
    }
}
