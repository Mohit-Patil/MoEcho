package com.example.mohit.moecho.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.mohit.moecho.R
import com.example.mohit.moecho.fragments.SongPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                NotificationBuilder.Statified.notificationManager?.cancel(1998)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (intent?.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.playing = true
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (intent?.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.playing = true
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        NotificationBuilder.Statified.notificationManager?.cancel(1998)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaplayer?.pause()
                            SongPlayingFragment.Statified.playing = true
                            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    // not in call
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean == false && SongPlayingFragment.Statified.playing) {

                            SongPlayingFragment.Statified.mediaplayer?.start()
                            SongPlayingFragment.Statified.playing = false
                            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {

                }
            }

        }
    }

}