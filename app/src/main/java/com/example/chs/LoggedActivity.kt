package com.example.chs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_logedin.*
import android.os.*
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_logedin.loggedActivity_WheaterPicture
import org.json.JSONObject
import java.lang.Exception
import java.lang.Runnable
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.truncate


//import androidx.fragment.app.DialogFragment

class LoggedActivity : AppCompatActivity() {
    private val address="Timisoara"
    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
    val mainHandler = Handler(Looper.getMainLooper())

    lateinit var applicationViewModel: ApplicationViewModel
    val compositeDisposable = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        getSchedule()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        applicationViewModel = ApplicationViewModel.instantiate(this)

//Remove notification bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_logedin)



        mainHandler.post(object : Runnable {
            override fun run() {
                updateTemperature()
                mainHandler.postDelayed(this, 30000)
            }
        })


            number_picker.setOnScrollListener() { t1: NumberPicker?, t2: Int ->
                if (t1 != null)
                    applicationViewModel.setTemperature(t1.value).subscribe() { t1: TemperatureResponse?, t2: Throwable? ->

                        findViewById<TextView>(R.id.temp2).text = t1?.temperature.toString()

                    }

            }


        getSchedule()
        number_picker.minValue = 5
        number_picker.maxValue = 28
        number_picker.value=20
        number_picker.wrapSelectorWheel = false

//        findViewById<TextView>(R.id.).text = address
//        number_picker.setOnValueChangedListener { picker, oldVal, newVal ->
//
//            //Display the newly selected number to text view
//            text_view.text = "Selected Value : $newVal"
//        }
        weatherTask().execute()

        logOutBtn.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            PreferencesController.token = ""
            startActivity(intent)
        }
        bluetooth_btn.setOnClickListener(){
            val intent=Intent(this,SelectDeviceActivity::class.java)
            startActivity(intent)
        }

        schedule_btn.setOnClickListener(){
            val intent = Intent(this,ScheduleActivity::class.java)
            startActivity(intent)

        }
    }



    fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }

    override fun onPause() {
        super.onPause()

        if(isFinishing) {
            compositeDisposable.dispose()
        }
    }

    fun updateTemperature(){
        compositeDisposable.add(

        applicationViewModel.getTemperature()
            .subscribe(){ t1: TemperatureResponse?, t2: Throwable? ->
                if(t1 !=null){
                    findViewById<TextView>(R.id.temp2).text = t1.temperature.toString()
                    number_picker.value = t1.setTemperature.toInt()
                    if(t1.isOn==true) isHeaterOn.text="Heating On"
                    else isHeaterOn.text="Heating Off"
                }
    })}

    fun getSchedule(){
        compositeDisposable.add(

            applicationViewModel.getSchedule()
                .subscribe(){ t1: Long?, t2: Throwable? ->
                    if(t1 !=null){
                        var time=Timestamp(t1*1000)
                        findViewById<TextView>(R.id.textViewScheduled).text = time.toLocaleString()
                    }
                })}





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
                var url="https://api.openweathermap.org/data/2.5/forecast?q=Bocsig&units=metric&cnt=9&APPID=${MainActivity.API}"
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
                val jsonObj = JSONObject(result).getJSONArray("list").getJSONObject(0)
                val jsonObjTomorrow = JSONObject(result).getJSONArray("list").getJSONObject(8)
                val mainTomorrow =  jsonObjTomorrow.getJSONObject("main")
                val main = jsonObj.getJSONObject("main")
                val sys: JSONObject = jsonObj.getJSONObject("sys")
                val weather: JSONObject = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updateAt: Long = jsonObj.getLong("dt")
                val temp = truncate(main.getString("temp").toDouble()).toString() + "°C"
                val weatherDescription = weather.getString("description")
                val icon = weather.getString("icon")



                /* Populating extracted data into our views */

                findViewById<TextView>(R.id.loggedActivity_WheatherStatus).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.loggedActivity_WheatherTemperature).text = temp
                findViewById<TextView>(R.id.loggedActivity_TemperatureTomorrow).text = truncate(mainTomorrow.getString("temp").toDouble()).toString()+"°C"

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                //picture
                Glide.with(this@LoggedActivity)
                    .load("http://openweathermap.org/img/wn/$icon@2x.png")
                    .into(loggedActivity_WheaterPicture)


            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

    }




}

