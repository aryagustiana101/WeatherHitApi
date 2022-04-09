package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.TextView
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class GetRequestCurrentWeather(
    private val context: Context,
    private val activity: MainActivity,
    private val url: String
) :
    AsyncTask<Void, Void, String>() {

    private lateinit var progressDialog: ProgressDialog

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
        progressDialog.setTitle("Fetch Current Weather")
        progressDialog.setMessage("Fetching Current Weather Please Wait...")
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Void?): String {
        return download()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        val resultObject: JSONObject = JSONTokener(result).nextValue() as JSONObject

        val cityName = resultObject.getString("name").toString()
        val main = resultObject.getJSONObject("main")

        val resultWeather = resultObject.getJSONArray("weather")[0].toString()
        val weather: JSONObject = JSONTokener(resultWeather).nextValue() as JSONObject

        val tvCityName = activity.findViewById<TextView>(R.id.tvCityName)
        tvCityName.text = cityName

        val tvDegreeValue =  activity.findViewById<TextView>(R.id.tvDegreeValue)
        tvDegreeValue.text = main.getString("temp").toDouble().toInt().toString()

        val tvFeelsLike =  activity.findViewById<TextView>(R.id.tvFeelsLike)
        tvFeelsLike.text = weather.getString("main").toString()

        val tvTemperatureMin =  activity.findViewById<TextView>(R.id.tvTemperatureMin)
        tvTemperatureMin.text = main.getString("temp_min").toDouble().toInt().toString()

        val tvTemperatureMax =  activity.findViewById<TextView>(R.id.tvTemperatureMax)
        tvTemperatureMax.text = main.getString("temp_max").toDouble().toInt().toString()

        progressDialog.dismiss()
    }
}