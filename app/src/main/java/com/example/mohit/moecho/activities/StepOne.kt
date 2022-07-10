package com.example.mohit.moecho.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.example.mohit.moecho.R


class StepOne : AppCompatActivity() {
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
                    var clickIntent = Intent(this@StepOne, StepTwo::class.java)
                    startActivity(clickIntent)
                    System.out.println("Has Connection")
                }
                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    var clickIntent = Intent(this@StepOne, StepTwo::class.java)
                    startActivity(clickIntent)
                    System.out.println("Has Connection")
                }
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }


    }

}