package com.example.android.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;

    @NonNull
    @Override
    public ForecastAdapter.ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int  listItemId = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemId, parent, false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        holder.mWeatherTextView.setText(this.mWeatherData[position]);
    }

    @Override
    public int getItemCount() {
        if(mWeatherData == null)
            return 0;
        else
            return mWeatherData.length;
    }

    public void setWeatherData(String[] weatherData) {
        this.mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
        }
    }
}
