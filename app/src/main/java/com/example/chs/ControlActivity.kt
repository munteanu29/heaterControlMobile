package com.example.chs

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ControlActivity: AppCompatActivity(){
    companion object{
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket:BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.control_layout)
//        m_address=intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)
//        ConnectToDevice(this).execute()
//        control_led_on.setOnClickListener{sendCommand("a")}
//        control_led_off.setOnClickListener{sendCommand("b")}
//        control_led_disconnect.setOnClickListener{disconnect()}
//
//    }
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.control_layout)
    m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)


    ConnectToDevice(this).execute()

    control_led_on.setOnClickListener { sendCommand(wifiId_field.text.toString() +","+ wifiPassword_field.text.toString()+","+userId_field.text.toString()+","+userPasswordField.text.toString()+"\n" ) }
    control_led_off.setOnClickListener { sendCommand("b") }
    control_led_disconnect.setOnClickListener { disconnect() }
}


    private fun sendCommand(input:String){
        if(m_bluetoothSocket!=null){
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e:IOException){
                e.printStackTrace()
            }
        }

    }
    private fun disconnect(){
        if(m_bluetoothSocket!=null){
            try{
            m_bluetoothSocket!!.close()
            m_bluetoothSocket=null
            m_isConnected=false
        }catch (e:IOException){
            e.printStackTrace()
            }
        }
        finish()

    }

    private class ConnectToDevice(c: Context):AsyncTask<Void, Void, String>(){
        private var connectSucces: Boolean = true
        private val context: Context
        init {
            this.context = c

        }
        override fun onPreExecute() {
            super.onPreExecute()
            m_progress=ProgressDialog.show(context,"Connecting...","pleas wait")
        }

        override fun doInBackground(vararg params: Void?): String? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            try{
                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
                    val device:BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket=device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }

            }catch (e:IOException){
                connectSucces=false
                e.printStackTrace()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSucces){
                Log.i("data","couldn't connect")

            }else{
                m_isConnected=true
            }
            m_progress.dismiss()
        }
    }
}