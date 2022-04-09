package com.example.weatherhitapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.*

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 100
            )
        }

        location

        val ivAuth = findViewById<ImageView>(R.id.ivAuth)

        ivAuth.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    @get:SuppressLint("MissingPermission")
    private val location: Unit
        get() {
            try {
                locationManager =
                    applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onLocationChanged(location: Location) {
        val latitude: String = location.latitude.toString()
        val longitude: String = location.longitude.toString()

        GetRequestCurrentWeather(
            this,
            this,
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=eb70a613199a216a927988f15ad684ce&units=metric"
        ).execute()

        GetRequestForecast(
            this,
            this,
            "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=eb70a613199a216a927988f15ad684ce&units=metric&cnt=6"
        ).execute()

        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            Log.i("Location", address)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}