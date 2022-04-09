package com.example.weatherhitapi

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("StaticFieldLeak")
@Suppress("DEPRECATION")
class DownloadTask(
    val context: Context,
    private val yourUrl: String,
    private val fileName: String)   : AsyncTask<Boolean, Void, Boolean>() {

    override fun doInBackground(vararg booleans: Boolean?): Boolean {

        val url = URL(this.yourUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connectTimeout = 25000
        connection.readTimeout = 25000
        connection.connect()
        val inputStream = connection.inputStream

        val lengthOfFile: Long = 0
        var status: Int

        try {

            val buff = ByteArray(1024 * 4)
            var downloaded: Long = 0

            val folder = File(Environment.getExternalStorageDirectory().toString() + "/DownloadTest/")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val documentFile = File("$folder/$fileName")
            documentFile.parentFile.mkdirs()
            try {
                documentFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var output: OutputStream? = null
            try {
                output = FileOutputStream(documentFile, false)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            while (true) {

                val readed = inputStream.read(buff)

                if (readed == -1) {
                    break
                }
                if (isCancelled) {
                    break
                }
                downloaded += readed.toLong()

                output!!.write(buff, 0, readed)

            }

            output!!.flush()
            output.close()
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    override fun onProgressUpdate(vararg values: Void) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(objects: Boolean?) {
        super.onPostExecute(objects)
    }

    override fun onCancelled(aBoolean: Boolean?) {
        super.onCancelled(aBoolean)
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/DjCensura/")
        val documentFile = File("$folder/$fileName")
        documentFile.delete()
    }

}