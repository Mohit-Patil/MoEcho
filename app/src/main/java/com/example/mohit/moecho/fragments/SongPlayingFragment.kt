package com.example.mohit.moecho.fragments


import android.app.Activity
import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.mohit.moecho.CurrentSongHelper
import com.example.mohit.moecho.R
import com.example.mohit.moecho.activities.MainActivity
import com.example.mohit.moecho.databases.EchoDatabase
import com.example.mohit.moecho.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.mohit.moecho.fragments.SongPlayingFragment.Staticated.playNext
import com.example.mohit.moecho.fragments.SongPlayingFragment.Staticated.processInformation
import com.example.mohit.moecho.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.example.mohit.moecho.fragments.SongPlayingFragment.Statified.myActivity
import com.example.mohit.moecho.fragments.SongPlayingFragment.Statified.updateSongTime
import com.example.mohit.moecho.songs
import com.example.mohit.moecho.utils.SeekBarControl
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.lang.Exception
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var isSongPlaying: Boolean = true
        var songTitileView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var fab: ImageButton? = null

        var currentSongHelper: CurrentSongHelper? = null

        var currentPosition: Int = 0
        var fetchSongs: ArrayList<songs>? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var favoriteContent: EchoDatabase? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var back: String? = null
        var counter: Int = 0
        var songplayinglinearlayout: LinearLayout? = null

        var updateSongTime = object : Runnable {
            override fun run() {
                try {
                    val getCurrent = Statified.mediaplayer?.currentPosition
                    Statified.startTimeText?.setText(
                        String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                            TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((getCurrent?.toLong() as Long)))
                        )
                    )
                    Statified.seekbar?.setProgress(getCurrent?.toInt() as Int)
                    Statified.isSongPlaying = true
                    Handler().postDelayed(this, 15)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }


        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {
            if (Statified.currentSongHelper?.isshuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)

                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextViews(
                        Statified.currentSongHelper?.songTitle as String,
                        Statified.currentSongHelper?.songArtist as String
                    )

                    Statified.mediaplayer?.reset()

                    try {
                        Statified.mediaplayer?.setDataSource(
                            Statified.myActivity,
                            Uri.parse(Statified.currentSongHelper?.songPath)
                        )
                        Statified.mediaplayer?.prepare()
                        System.out.println("Song Played")
                        Statified.mediaplayer?.start()
                        processInformation(Statified.mediaplayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleupdated = songTitle
            if (songTitle.equals("<unknown>", true)) {
                songTitleupdated = "Unknown"
            }

            var songArtistupdated = songArtist
            if (songArtist.equals("<unknown>", true)) {
                songArtistupdated = "Unknown"
            }
            Statified.songTitileView?.setText(songTitleupdated)
            Statified.songArtistView?.setText(songArtistupdated)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekbar?.max = finalTime
            Statified.startTimeText?.setText(
                String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                )
            )
            Statified.endTimeText?.setText(
                String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
                )
            )
            Statified.seekbar?.setProgress(startTime)

            Handler().postDelayed(updateSongTime, 15)
        }
        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1

            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition

            }
            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songId = nextSong?.songID as Long
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            Statified.currentSongHelper?.isLoop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            editorLoop?.putBoolean("feature", false)
            editorLoop?.apply()
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }

            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(
                    Statified.myActivity,
                    Uri.parse(Statified.currentSongHelper?.songPath) as Uri
                )
                Statified.mediaplayer?.prepare()
                System.out.println("Song Played")
                Statified.mediaplayer?.start()
                Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
                processInformation(Statified.mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
            }
        }
        fun playPrevious() {
            Statified.currentPosition = Statified.currentPosition - 1
            if (Statified.currentPosition == -1) {
                Statified.currentPosition = 0
            }
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )


            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                System.out.println("Song Played")
                Statified.mediaplayer?.start()
                Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
                processInformation(Statified.mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
            }
        }


    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        MainActivity.Statified.IS_MUSIC_SCREEN_MAIN = true
        activity?.title = "Now Playing"
        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitileView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favouriteIcon)
        Statified.songplayinglinearlayout = view?.findViewById(R.id.songplaying)
        Statified.fab?.alpha = 0.8f


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Statified.audioVisualization = Statified.glView as AudioVisualization
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(
            Statified.mSensorListener, Statified.mSensorManager?.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()

        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }


    override fun onDestroyView() {
        Statified.audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favoriteContent = EchoDatabase(Statified.myActivity)


        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isshuffle = false
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)


        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var fromFavBottomBar: String? = null
        var fromMainBottomBar: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songArtist = arguments?.getString("songArtist")
            _songTitle = arguments?.getString("songTitle")
            songId = arguments!!.getInt("SongId")!!.toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments!!.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            fromFavBottomBar = arguments?.get("FavBottomBar") as? String
            fromMainBottomBar = arguments?.get("MainBottomBar") as? String


            Staticated.updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (fromFavBottomBar != null) {
            Statified.mediaplayer = FavouriteFragment.Statified.mediaPlayer
            Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Activity,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Activity,
                        R.drawable.favorite_off
                    )
                )
            }
        } else if (fromMainBottomBar != null) {
            Statified.mediaplayer = MainScreenFragment.Statified.mediaPlayer
            Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Activity,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Activity,
                        R.drawable.favorite_off
                    )
                )
            }
        } else {
            Statified.mediaplayer = MediaPlayer()
            Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(path) as Uri)
                Statified.mediaplayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaplayer?.start()
            Statified.currentSongHelper?.isPlaying = true
        }



        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)

        if (Statified.mediaplayer?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        } else {
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }
        Statified.mediaplayer?.setOnCompletionListener {
            onSongComplete()
        }
        clickHandler()


        var prefsforShuffle =
            Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsforShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isshuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isshuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsforLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsforLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isshuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            Statified.currentSongHelper?.isLoop = false
        }
        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myActivity as Context,
                    R.drawable.favorite_on
                )
            )
        } else {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myActivity as Context,
                    R.drawable.favorite_off
                )
            )
        }
        seekbarHandler()

    }

    fun clickHandler() {
        Statified.fab?.setOnClickListener({
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
                Statified.favoriteContent?.deleteFavorite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity, "Removed From Favorites", Toast.LENGTH_SHORT).show()
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myActivity as Context,
                        R.drawable.favorite_on
                    )
                )
                Statified.favoriteContent?.storeAsFavourite(
                    Statified.currentSongHelper?.songId?.toInt(),
                    Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle,
                    Statified.currentSongHelper?.songPath
                )
                Toast.makeText(Statified.myActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
            }
        })
        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (Statified.currentSongHelper?.isshuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isshuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()

            } else {
                Statified.currentSongHelper?.isshuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })

        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            if (Statified.currentSongHelper?.isshuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        })

        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            Staticated.playPrevious()
        })

        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop =
                Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isshuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()


            }
        })

        Statified.playpauseImageButton?.setOnClickListener({
            try {
                if (Statified.mediaplayer?.isPlaying as Boolean) {
                    Statified.mediaplayer?.pause()
                    Statified.currentSongHelper?.isPlaying = false
                    Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                } else {
                    System.out.println("Song Played")
                    Statified.mediaplayer?.start()
                    Statified.currentSongHelper?.isPlaying = true
                    Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                    Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                var pos = 0
                if (Statified.back.equals("Favorite", true)) {
                    pos = 0
                }
                if (Statified.back.equals("MainScreen", true)) {
                    pos = 1
                }
                if (pos == 1) {
                    val mainScreenFragment = MainScreenFragment()
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
                }
                if (pos == 0) {
                    val favoriteFragment = FavouriteFragment()
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, favoriteFragment)
                        .commit()
                }
                return false
            }
        }
        return false
    }


    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 12) {
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean && Statified.isSongPlaying == true) {
                        Statified.currentSongHelper?.isPlaying = true
                        if (Statified.currentSongHelper?.isLoop as Boolean) {
                            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                        }
                        if (Statified.currentSongHelper?.isshuffle as Boolean) {
                            Staticated.playNext("PlayNextLikeNormalShuffle")
                        } else {
                            Staticated.playNext("PlayNextNormal")
                        }
                        Statified.isSongPlaying = false
                    } else {
                    }
                }
            }

        }
    }
    fun seekbarHandler() {
        val seekbarListener = SeekBarControl()
        Statified.seekbar?.setOnSeekBarChangeListener(seekbarListener)
        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)
    }


}
