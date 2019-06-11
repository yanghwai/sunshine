package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;
    private RecyclerView mForecastRecyclerView;

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

        loadWeatherData();
    }

    /* For create menu buttons*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void showWeatherDataView(){
        mForecastRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessageView(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mForecastRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void loadWeatherData() {
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
        FetchWeatherTask asyncTask = new FetchWeatherTask();
        asyncTask.execute(location);
    }

    @Override
    public void onClick(String weather) {
        Toast.makeText(this, weather, Toast.LENGTH_LONG).show();
    }

    /* Async task class for querying weather data from API*/
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            if(strings.length == 0)
                return null;

            String location = strings[0];
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
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            /* Make loading bar invisible*/
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if(weatherData != null){
                showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
            }else{
                showErrorMessageView();
            }
        }
    }
}
