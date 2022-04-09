package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class PostRequestLogin(
    private var context: Context,
    private var activity: LoginActivity,
    private var email: String,
    private var password: String
) : AsyncTask<Void, Void, String>() {


    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharedPreferences

    private fun connect(): Any {
        try {
            val url = URL("https://reqres.in/api/login")
            val conn = url.openConnection() as HttpURLConnection
            val uriBuilder = Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password)
                .build()
            val params = uriBuilder.toString().replace("?", "")
            val postData = params.toByteArray(StandardCharsets.UTF_8)
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true
            val dataOutputStream = DataOutputStream(conn.outputStream)
            dataOutputStream.write(postData)
            return conn
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return "URL ERROR" + e.message
        } catch (e: IOException) {
            e.printStackTrace()
            return "CONNECT ERROR" + e.message
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
        progressDialog.setTitle("Login")
        progressDialog.setMessage("Logging You In Please Wait...")
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Void?): String {
        return download()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        progressDialog.dismiss()

        val resultObject: JSONObject = JSONTokener(result).nextValue() as JSONObject

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()

        sharedPreferences = SharedPreferences(context)
        sharedPreferences.signature = resultObject.getString("token").toString()
        Log.i("sharedPref", sharedPreferences.signature.toString())
    }

}