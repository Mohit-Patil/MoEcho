package com.example.mohit.moecho.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.mohit.moecho.R
import com.example.mohit.moecho.activities.MainActivity
import com.example.mohit.moecho.fragments.SongPlayingFragment
import java.lang.Exception


class NotificationBuilder : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val ACTION_PLAY = "action_play"
    val ACTION_PAUSE = "action_pause"
    val ACTION_REWIND = "action_rewind"
    val ACTION_FAST_FORWARD = "action_fast_foward"
    val ACTION_NEXT = "action_next"
    val ACTION_PREVIOUS = "action_previous"
    val ACTION_STOP = "action_stop"
    private val mManager: MediaSessionManager? = null
    private var mSession: MediaSession? = null
    private var mController: MediaController? = null
    private var channelId = "com.example.mohit.moecho.activities"
    var builder: NotificationCompat.Builder? = null

    object Statified {
        var notificationManager: NotificationManager? = null
    }


    private fun handleIntent(intent: Intent?) {
        if (intent == null || intent.action == null)
            return

        val action = intent.action

        if (action!!.equals(ACTION_PLAY, ignoreCase = true)) {

            mController?.getTransportControls()?.play()
        } else if (action.equals(ACTION_PAUSE, ignoreCase = true)) {
            mController?.getTransportControls()?.pause()
        } else if (action.equals(ACTION_FAST_FORWARD, ignoreCase = true)) {
            mController?.getTransportControls()?.fastForward()
        } else if (action.equals(ACTION_REWIND, ignoreCase = true)) {
            mController?.getTransportControls()?.rewind()
        } else if (action.equals(ACTION_PREVIOUS, ignoreCase = true)) {
            mController?.getTransportControls()?.skipToPrevious()
        } else if (action.equals(ACTION_NEXT, ignoreCase = true)) {
            mController?.getTransportControls()?.skipToNext()
        } else if (action.equals(ACTION_STOP, ignoreCase = true)) {
            mController?.getTransportControls()?.stop()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mManager == null) {
            initMediaSessions()
        }
        try {
            handleIntent(intent)
        }catch (e:Exception){
            e.printStackTrace()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun initMediaSessions() {

        mSession = MediaSession(applicationContext, "simple player session")
        mController = MediaController(applicationContext, mSession!!.sessionToken)

        mSession!!.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                super.onPlay()
                SongPlayingFragment.Statified.mediaplayer?.start()
                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true
                SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                Log.e("NotificationBuilder", "onPlay")
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE))
            }

            override fun onPause() {
                super.onPause()
                SongPlayingFragment.Statified.mediaplayer?.pause()
                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = false
                SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                Log.e("NotificationBuilder", "onPause")
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY))
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.e("NotificationBuilder", "onSkipToNext")
                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true
                if (SongPlayingFragment.Statified.currentSongHelper?.isshuffle as Boolean) {
                    SongPlayingFragment.Staticated.playNext("PlayNextLikeNormalShuffle")
                } else {
                    SongPlayingFragment.Staticated.playNext("PlayNextNormal")
                }
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE))
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                Log.e("NotificationBuilder", "onSkipToPrevious")
                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true
                SongPlayingFragment.Staticated.playPrevious()
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE))
            }


            override fun onStop() {
                super.onStop()
                Log.e("NotificationBuilder", "onStop")
                val notificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
                val intent = Intent(applicationContext, NotificationBuilder::class.java)
                stopService(intent)
            }
        }
        )
    }

    private fun generateAction(icon: Int, title: String, intentAction: String): NotificationCompat.Action {
        val intent = Intent(applicationContext, NotificationBuilder::class.java)
        intent.action = intentAction
        val pendingIntent = PendingIntent.getService(applicationContext, 1, intent, 0)
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    @SuppressLint("NewApi", "InlinedApi")
    fun buildNotification(action: NotificationCompat.Action) {
        val description = mController?.metadata?.description
        val playbackState = mController?.playbackState
        val style = android.support.v4.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView()
            .setShowCancelButton(true)

        val intent = Intent(this, MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(),
            intent, 0
        )
        try {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                var notificationChannel = NotificationChannel(channelId, "MoEcho", NotificationManager.IMPORTANCE_HIGH)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Statified.notificationManager?.createNotificationChannel(notificationChannel)
                }
                builder = NotificationCompat.Builder(this, channelId)
                    .setColorized(true)
                    .setColor(Color.parseColor("#9D2A58"))
                    .setSmallIcon(R.drawable.echo_logo)
                    .setContentTitle(SongPlayingFragment.Statified?.currentSongHelper?.songTitle)
                    .setContentText(SongPlayingFragment.Statified?.currentSongHelper?.songArtist)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setStyle(style)
            } else {
                builder = NotificationCompat.Builder(this)
                    .setColorized(true)
                    .setColor(Color.parseColor("#9D2A58"))
                    .setSmallIcon(R.drawable.echo_logo)
                    .setContentTitle(SongPlayingFragment.Statified?.currentSongHelper?.songTitle)
                    .setContentText(SongPlayingFragment.Statified?.currentSongHelper?.songArtist)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle(style)
            }

            builder?.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS))
            builder?.addAction(action)
            builder?.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT))
            style.setShowActionsInCompactView(0, 1, 2, 3)
            Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, builder?.build())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        mSession?.release()
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
            super.onTaskRemoved(rootIntent)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}