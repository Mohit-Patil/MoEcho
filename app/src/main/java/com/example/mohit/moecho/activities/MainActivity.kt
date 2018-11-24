package com.example.mohit.moecho.activities

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.mohit.moecho.R
import com.example.mohit.moecho.activities.MainActivity.Statified.notificationManager
import com.example.mohit.moecho.adapters.NavigationDrawerAdapter
import com.example.mohit.moecho.fragments.MainScreenFragment
import com.example.mohit.moecho.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {

    private var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    private var imagesForNavdrawer = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )
    private var trackNotificationBuilder: Notification? = null
    private var notificationChannel: NotificationChannel? = null
    private var channelId = "com.example.mohit.moecho.activities"
    var description = "Song Playing Notification"

    @SuppressLint("StaticFieldLeak")
    object Statified {
        @SuppressLint("StaticFieldLeak")
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManager? = null
        var IS_MUSIC_SCREEN_MAIN = false
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Me")


        val toggle = ActionBarDrawerToggle(
            this@MainActivity, MainActivity.Statified.drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        MainActivity.Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        val mainScreenFragment = MainScreenFragment()


        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
            .commit()



        val _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, imagesForNavdrawer, this)
        _navigationAdapter.notifyDataSetChanged()

        val navigation_recycler_view = findViewById<RecyclerView>(R.id.navigationrecyclerview)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var intent = Intent(this@MainActivity, MainActivity::class.java)
        var pintent = PendingIntent.getActivity(
            this@MainActivity, System.currentTimeMillis().toInt(),
            intent, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(notificationChannel)
            trackNotificationBuilder = Notification.Builder(this, channelId)
                .setContentTitle("A track is playing in the background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pintent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()

        } else {
            trackNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in the background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pintent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(1998)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1998, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(1998)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        try {
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1998, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }


}



