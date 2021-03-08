package com.example.chs

import android.annotation.TargetApi
import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_logedin.*
import kotlinx.android.synthetic.main.activity_logedin.schedule_btn
import kotlinx.android.synthetic.main.activity_schedule.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ScheduleActivity : AppCompatActivity() {

    lateinit var applicationViewModel: ApplicationViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        applicationViewModel = ApplicationViewModel.instantiate(this)

        schedule_btn.setOnClickListener(){
            var hour= datePicker1.currentHour
            var minute = datePicker1.currentMinute
            if(temperatureInput.text.isNotEmpty()){
            var temperature: Float =  temperatureInput.text.toString().toFloat()


                var heaterSchedule = HeaterSchedule(
                    Hour = hour,
                    Minute = minute,
                    FinalHouseTemperature = temperature
                )
                applicationViewModel.schedule(heaterSchedule)
                    .subscribe() { t1: HeaterTimeResponse?, t2: Throwable? ->
                        val intent = Intent(this, LoggedActivity::class.java)
                        startActivity(intent)
                    }
            }

        }





        }


//        number_picker.setOnScrollListener(){ t1: NumberPicker?, t2: Int ->
//            if(t1 !=null)
//                applicationViewModel.setTemperature(t1.value).subscribe(){t1: TemperatureResponse?, t2: Throwable? ->
//
//                    if(t1 !=null && t1.isOn==true) isHeaterOn.text="Heating On"
//                    else isHeaterOn.text="Heating Off"
//
//                    findViewById<TextView>(R.id.temp2).text = t1?.temperature.toString()
//
//                }
//
//
//    }
}
