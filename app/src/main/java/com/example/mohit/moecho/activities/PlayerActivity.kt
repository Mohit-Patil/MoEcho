package com.example.mohit.moecho.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.mohit.moecho.R
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerView


class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    var youTubeView: YouTubePlayerView? = null
    val YOUTUBE_API_KEY = "AIzaSyAecm5XasJhbVEXzp_XHZ8_R3p166R186Y"
    var playerStateChangeListener: MyPlayerStateChangeListener? = null
    var playbackEventListener: MyPlaybackEventListener? = null
    var dialog: ProgressDialog? = null

    val youTubePlayerProvider: Provider?
        get() = youTubeView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        youTubeView = findViewById(R.id.youtube_view)
        youTubeView!!.initialize(YOUTUBE_API_KEY, this)
        playerStateChangeListener = MyPlayerStateChangeListener()
        playbackEventListener = MyPlaybackEventListener()
        dialog = ProgressDialog(this)
        dialog?.setMessage("Please wait while the song is loading")
        dialog?.show()


    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, wasRestored: Boolean) {
        player.setPlayerStateChangeListener(playerStateChangeListener)
        player.setPlaybackEventListener(playbackEventListener)

        if (!wasRestored) {
            // player.play();
            player.loadVideo(StepTwo.Statified.qq)
            dialog?.dismiss()
            //player.play()
        }
    }

    override fun onInitializationFailure(provider: Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show()
        } else {
            val error = String.format(getString(R.string.player_error), errorReason.toString())
            //Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            youTubePlayerProvider!!.initialize(YOUTUBE_API_KEY, this)
        }
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    inner class MyPlaybackEventListener : YouTubePlayer.PlaybackEventListener {

        override fun onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing")
        }

        override fun onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused")
        }

        override fun onStopped() {
            // Called when playback stops for a reason other than being paused.
            //L̥showMessage("Stopped")
        }

        override fun onBuffering(b: Boolean) {
            // Called when buffering starts or ends.
        }

        override fun onSeekTo(i: Int) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    inner class MyPlayerStateChangeListener : YouTubePlayer.PlayerStateChangeListener {

        override fun onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        override fun onLoaded(s: String) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        override fun onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        override fun onVideoStarted() {
            // Called when playback of the video starts.
        }

        override fun onVideoEnded() {
            // Called when the video reaches its end.
        }

        override fun onError(errorReason: YouTubePlayer.ErrorReason) {
            // Called when an error occurs.
        }
    }

    companion object {
        val RECOVERY_REQUEST = 1
    }


}