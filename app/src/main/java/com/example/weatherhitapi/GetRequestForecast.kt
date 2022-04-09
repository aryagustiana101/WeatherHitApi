package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.os.AsyncTask
import android.widget.ListView

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class GetRequestForecast(private val context: Context, private val activity: MainActivity, private val url: String) :
AsyncTask<Void, Void, String>() {

    private lateinit var lvForecast : ListView
    private lateinit var progressDialog: ProgressDialog

    private val forecasts = mutableListOf<ForecastData>()

    private fun connect(): Any {
        return try {
            val url = URL(this.url)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.doInput = true
            conn
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            "URL ERROR" + e.message
        } catch (e: IOException) {
            e.printStackTrace()
            "CONNECT ERROR" + e.message
        }
    }

    private fun download(): String {
        val connection = connect()
        try {
            val conn = connection as HttpURLConnection
            if (conn.responseCode == 200) {
                val inputStream = BufferedInputStream(conn.inputStream)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val result = StringBuffer()
                var line: String?
                do {
                    line = bufferedReader.readLine()
                    if (line == null) {
                        break
                    }
                    result.append(line + "\n")
                } while (true)
                bufferedReader.close()
                inputStream.close()
                return result.toString()
            } else {
                return "Error " + conn.responseMessage + "\n" + conn.responseCode
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error" + e.message
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Fetch Weather Forecasts")
        progressDialog.setMessage("Fetching Weather Forecasts Please Wait...")
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Void?): String {
        return download()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        val resultObject: JSONObject = JSONTokener(result).nextValue() as JSONObject

        val list = resultObject.getJSONArray("list")

        for (i in 0 until list.length()) {

            val main = list.getJSONObject(i).getJSONObject("main")
            val weather = list.getJSONObject(i).getJSONArray("weather")

            val dt = list.getJSONObject(i).getString("dt").toString()
            val icon = weather.getJSONObject(0).getString("icon").toString()
            val temp = main.getString("temp").toString()
            val feelsLike = main.getString("feels_like").toString()

            forecasts.add(ForecastData(dt, icon, temp, feelsLike))
        }

        lvForecast = activity.findViewById(R.id.lvForecast)
        lvForecast.adapter = ListViewAdapter(context, R.layout.row_forecast, forecasts)
        progressDialog.dismiss()
    }
}