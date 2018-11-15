package com.example.mohit.moecho.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.mohit.moecho.R

class SplashActivity : AppCompatActivity() {
    var permissionsString = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.RECORD_AUDIO
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!hasPermissions(this@SplashActivity, *permissionsString)) {
            //We have to Ask for Permissions
            ActivityCompat.requestPermissions(this@SplashActivity, permissionsString, 131)
        } else {
            Handlr(this@SplashActivity)


        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Handlr(this@SplashActivity)
                } else {
                    Toast.makeText(this@SplashActivity, "Please Grant All the Permissions", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@SplashActivity, "Something went Wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }

        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        var hasAllpermisiions = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllpermisiions = false
            }
        }
        return hasAllpermisiions
    }

    fun Handlr(context: Context) {
        Handler().postDelayed({
            val startAct = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(startAct)
            this.finish()
        }, 1000)
    }
}
