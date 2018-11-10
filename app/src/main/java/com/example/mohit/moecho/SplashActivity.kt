package com.example.mohit.moecho

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {
    var permissionsString = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                                    Manifest.permission.RECORD_AUDIO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
    fun hasPermissions(context: Context, vararg permissions: String): Boolean{
        var hasAllpermisiions = true
        for(permission in permissions){
            val res = context.checkCallingOrSelfPermission(permission)
            if(res != PackageManager.PERMISSION_GRANTED){
                hasAllpermisiions = false
            }
        }
        return hasAllpermisiions
    }
}
