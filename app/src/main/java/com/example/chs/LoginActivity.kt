package com.example.chs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

//    var passw

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        val applicationViewModel = ApplicationViewModel.instantiate(this)

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener{
            val intent= Intent(this,LoggedActivity::class.java)
            var username: TextView = findViewById(R.id.username)
            var password:  TextView= findViewById(R.id.password)
                println(username.text)
            if(username.text.length !=0 && password.text.length !=0) {
                applicationViewModel.login(username.text.toString(), password.text.toString())
                    .subscribe() { t1: LoginResponse?, t2: Throwable? ->

                        if(t1 != null) {
                            PreferencesController.token=t1.token
                            startActivity(intent)
                            finish()

                        }

                    }
            }

        }
    }

}
