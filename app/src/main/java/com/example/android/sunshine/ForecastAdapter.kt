package com.example.android.sunshine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.sunshine.ForecastAdapter.ForecastAdapterViewHolder

class ForecastAdapter(private val mHandler: ForecastAdapterOnClickHandler) : RecyclerView.Adapter<ForecastAdapterViewHolder>() {

    private var mWeatherData = arrayOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapterViewHolder {
        val context = parent.context
        val listItemId = R.layout.forecast_list_item
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(listItemId, parent, false)
        return ForecastAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastAdapterViewHolder, position: Int) {
        holder.mWeatherTextView.text = mWeatherData[position]
    }

    override fun getItemCount(): Int {
        return mWeatherData.size
    }

    fun setWeatherData(weatherData: Array<String>) {
        mWeatherData = weatherData
        notifyDataSetChanged()
    }

    /* Implement OnClickListener to respond to click event*/
    inner class ForecastAdapterViewHolder(itemView: View) : ViewHolder(itemView), View.OnClickListener {
        val mWeatherTextView: TextView = itemView.findViewById(R.id.tv_weather_data)
        override fun onClick(view: View) {
            val position = adapterPosition
            val weather = mWeatherData[position]
            mHandler.onClick(weather)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface ForecastAdapterOnClickHandler {
        fun onClick(weather: String?)
    }
}