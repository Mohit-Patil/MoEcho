package com.example.mohit.moecho.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.mohit.moecho.R
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.Toast


class StepOne: AppCompatActivity(){
    var steponenext: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stepone)
        steponenext = findViewById(R.id.steponenext)
        steponenext?.setOnClickListener {
            val manager = applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = manager.activeNetworkInfo
            if (null != activeNetwork) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    var clickIntent = Intent(this@StepOne,StepTwo::class.java)
                    startActivity(clickIntent)
                    System.out.println("Has Connection")
                }
                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    var clickIntent = Intent(this@StepOne,StepTwo::class.java)
                    startActivity(clickIntent)
                    System.out.println("Has Connection")
                }
            } else {
                Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
        }


    }

}