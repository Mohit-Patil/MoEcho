package com.example.mohit.moecho.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.mohit.moecho.R
import com.example.mohit.moecho.adapters.FavoriteAdapter
import com.example.mohit.moecho.databases.EchoDatabase
import com.example.mohit.moecho.songs
import java.lang.Exception

class FavouriteFragment : Fragment() {
    var myActivity: Activity? = null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null
    var refreshList: ArrayList<songs>? = null
    var getListfromDatabase: ArrayList<songs>? = null
    var visibleFav: RelativeLayout? = null
    var _favScreenAdapter: FavoriteAdapter? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Favorites"
        noFavorites = view?.findViewById(R.id.nofavorites)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenbarfavscreen)
        songTitle = view.findViewById(R.id.songTitlefavScreen)
        songTitle?.setSelected(true)
        playPauseButton = view.findViewById(R.id.playpausebuttonfav)
        recyclerView = view.findViewById(R.id.favoriteRecycler)
        visibleFav = view.findViewById(R.id.visiblefav)
        songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
        SongPlayingFragment.Statified.mediaplayer?.setOnCompletionListener {
            SongPlayingFragment.Staticated.onSongComplete()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            _favScreenAdapter?.notifyDataSetChanged()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
        if (SongPlayingFragment.Statified?.mediaplayer?.isPlaying == null || SongPlayingFragment.Statified?.mediaplayer?.isPlaying == false) {
            nowPlayingBottomBar?.layoutParams?.height = 0
            visibleFav?.layoutParams?.height = -2
        }
        display_favorites_by_searching()
        val fm = fragmentManager
        for (entry in 0 until fm!!.backStackEntryCount) {
            fm.popBackStack()
            Log.d("hello", "Found fragment: " + fm.getBackStackEntryAt(entry).id)
        }

        if (SongPlayingFragment.Statified.currentSongHelper?.isPlaying == true)
            bottomBarSetup()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    override fun onResume() {
        super.onResume()
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

        return arrayList

    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()


            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            FavouriteFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaplayer
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
            args.putString("FavBottomBar", "success")
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

    fun display_favorites_by_searching() {
        if (favoriteContent?.checkSize() as Int > 0) {
            refreshList = ArrayList<songs>()
            getListfromDatabase = favoriteContent?.queryDBList()
            var fetchListfromDevice = getSongsFromPhone()
            if (fetchListfromDevice != null) {
                for (i in 0..fetchListfromDevice?.size - 1) {
                    for (j in 0..getListfromDatabase?.size as Int - 1) {
                        if ((getListfromDatabase?.get(j)?.songID) == (fetchListfromDevice?.get(i).songID)) {
                            refreshList?.add((getListfromDatabase as ArrayList<songs>)[j])
                        }
                    }
                }
            } else {

            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
                var favoriteAdapter = FavoriteAdapter(refreshList as ArrayList<songs>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)

                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)

            }


        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE


        }
    }


}
