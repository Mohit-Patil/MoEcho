package com.example.mohit.moecho.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.mohit.moecho.R
import com.example.mohit.moecho.adapters.MainScreenAdapter
import com.example.mohit.moecho.songs
import kotlinx.android.synthetic.main.fragment_main_screen.*
import java.lang.Exception
import java.util.*
import android.view.ViewGroup
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display

class MainScreenFragment : Fragment() {
    var getSongsList: ArrayList<songs>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: TextView? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null
    var trackPosition: Int = 0

    object Statified {
        var mediaPlayer: MediaPlayer? = null
        var sizeofarr: Int? = null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity?.title = "All Songs"

        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById(R.id.nosongs)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenbarmainscreen)
        songTitle = view?.findViewById<TextView>(R.id.songTitleMainScreen)
        songTitle?.setSelected(true)
        playPauseButton = view?.findViewById<ImageButton>(R.id.playpausebutton)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromPhone()
        val windowManager: WindowManager? = null
        val mLayoutManager = LinearLayoutManager(myActivity)
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")

        if (SongPlayingFragment.Statified?.mediaplayer?.isPlaying == null || SongPlayingFragment.Statified?.mediaplayer?.isPlaying == false) {
            hiddenbarmainscreen?.layoutParams?.height = 0
            visibleLayout?.layoutParams?.height = -2
        }
        if (Statified.sizeofarr == 0) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<songs>, myActivity as Context)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }


        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", true)) {
                Collections.sort(getSongsList, songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", true)) {
                Collections.sort(getSongsList, songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }

        bottomBarSetup()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, songs.Statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false

        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "false")
            editor?.putString("action_sort_recent", "true")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }

        return super.onOptionsItemSelected(item)
    }

    fun getSongsFromPhone(): ArrayList<songs> {
        var arrayList = ArrayList<songs>()
        var contentResolver = myActivity?.contentResolver
        var songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songuri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {

                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(songs(currentId, currentTitle, currentArtist, currentData, currentDate))

            }

        }
        songCursor?.close()
        Statified.sizeofarr = arrayList.size

        return arrayList

    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
            SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener {
                SongPlayingFragment.Staticated.onSongComplete()
                songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
                _mainScreenAdapter?.notifyDataSetChanged()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            MainScreenFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaplayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt(
                "songPosition",
                SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int
            )
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("MainBottomBar", "success")
            songPlayingFragment.arguments = args
            fragmentManager!!.beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack(null)
                .commit()


        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaplayer?.pause()
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                trackPosition = SongPlayingFragment.Statified.mediaplayer?.getCurrentPosition() as Int
            } else {
                SongPlayingFragment.Statified.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

            }
        })
    }
}
