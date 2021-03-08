package com.example.chs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chs.location.LocationHandler
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.truncate

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_ID = 111
        val API: String = "53e81050fa084937ff89f0b6eaf85cd7"
        var LAT: String? = null
        var LONG: String? = null
    }




    private var locationCallback: LocationCallback? = null
    private lateinit var locationHandler: LocationHandler
    private val isLocationPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(PreferencesController.token.isNotEmpty()){
            val intent = Intent(this, LoggedActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_main)
        this.locationHandler = LocationHandler(this)
        if (!isLocationPermissionGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ID
            )
        }
        setupLocation()

        logIn.setOnClickListener{
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }


    }



    private fun setupLocation() {

        this.locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult!!.lastLocation
                LAT = location.latitude.toString()
                LONG = location.longitude.toString()
                weatherTask().execute()

            }
        }
        locationHandler.registerLocationListener(locationCallback!!)



//        logIn.setOnClickListener(startActivity<LoginActivity>())
    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

        }



        override fun doInBackground(vararg params: String?): String? {
            var response: String?

            try {
                var url="https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LONG&units=metric&appid=$API"
                response =
                    URL(url).readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }

            return response
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            print(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys: JSONObject = jsonObj.getJSONObject("sys")
                val weather: JSONObject = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updateAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updateAt * 1000)
                    )
                val temp =truncate(main.getString("temp").toDouble()).toString() + "°C"
                val tempMin = "Min Temp:" + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp" + main.getString("temp_max") + "°C"

                val weatherDescription = weather.getString("description")
                val icon = weather.getString("icon")


                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.loggedActivity_WheatherStatus).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.loggedActivity_WheatherTemperature).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                //picture
                Glide.with(this@MainActivity)
                    .load("http://openweathermap.org/img/wn/$icon@2x.png")
                    .into(loggedActivity_WheaterPicture)


            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

    }


}
