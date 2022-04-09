package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.net.HttpURLConnection
import java.net.URL
import java.time.*
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class ListViewAdapter(private var mCtx: Context, private var resource: Int, private var items: List<ForecastData>) :
    ArrayAdapter<ForecastData>(mCtx, resource, items) {

    @SuppressLint("ViewHolder")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val tvForecastTime = view.findViewById<TextView>(R.id.tvForecastTime)
        val tvForecastTemp = view.findViewById<TextView>(R.id.tvForecastTemp)
        val tvForecastFeelsLike = view.findViewById<TextView>(R.id.tvForecastFeelsLike)

        val forecast: ForecastData = items[position]

        class SendHttpRequestTask : AsyncTask<String?, Void?, Bitmap?>() {
            override fun doInBackground(vararg p0: String?): Bitmap? {
                try {
                    val url = URL("http://openweathermap.org/img/wn/" + forecast.icon + "@2x.png")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connectTimeout = 25000
                    connection.readTimeout = 25000
                    connection.connect()
                    val input = connection.inputStream
                    return BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    Log.e("tag", e.message!!)
                }
                return null
            }
            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                val ivForecastIcon = view.findViewById<ImageView>(R.id.ivForecastIcon)
                ivForecastIcon.setImageBitmap(result)
            }
        }

        SendHttpRequestTask().execute()

        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(forecast.dt.toLong()),
            ZoneId.systemDefault()
        )
        val parsedDate = LocalDateTime.parse(dateTime.toString(), DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("hh:mm a"))

        tvForecastTime.text = formattedDate
        tvForecastTemp.text = forecast.temp.toDouble().toInt().toString()
        tvForecastFeelsLike.text = forecast.feels_like.toDouble().toInt().toString()

        return view
    }
}