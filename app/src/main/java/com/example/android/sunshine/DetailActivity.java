package com.example.android.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private TextView mWeatherDetailTextView;
    private String mWeatherDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mWeatherDetailTextView = findViewById(R.id.tv_weather_detail);

        Intent invokerIntent = getIntent();
        if(invokerIntent.hasExtra(Intent.EXTRA_TEXT)){
            mWeatherDetail = invokerIntent.getStringExtra(Intent.EXTRA_TEXT);
            mWeatherDetailTextView.setText(mWeatherDetail);
        }
    }

    /* Create and return an intent for sharing forecast*/
    private Intent getShareWeatherIntent(){
        return ShareCompat.IntentBuilder.from(DetailActivity.this)
                .setChooserTitle("Choose an app to share")
                .setText(mWeatherDetail + FORECAST_SHARE_HASHTAG)
                .setType("text/plain")
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        item.setIntent(getShareWeatherIntent());
        return true;
    }

}
