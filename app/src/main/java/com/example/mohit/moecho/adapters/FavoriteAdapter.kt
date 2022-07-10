package com.example.mohit.moecho.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.mohit.moecho.R
import com.example.mohit.moecho.fragments.SongPlayingFragment
<<<<<<< HEAD
import com.example.mohit.moecho.resources.songs
=======
import com.example.mohit.moecho.Songs
>>>>>>> master

class FavoriteAdapter(_songdetails: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    private var songDetails: ArrayList<Songs>? = null
    private var mContext: Context? = null

    init {
        this.songDetails = _songdetails
        this.mContext = _context
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val songObject = songDetails?.get(p1)
        if (songObject?.songTitle.equals("<unknown>", true)) {
            songObject?.songTitle = "Unknown"
        }
        if (songObject?.artist.equals("<unknown>", true)) {
            songObject?.artist = "Unknown"
        }
        p0.trackTitle?.text = songObject?.songTitle
        p0.trackArtist?.text = songObject?.artist
        p0.contentHolder?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
<<<<<<< HEAD
            var args = Bundle()
=======
            val args = Bundle()
>>>>>>> master
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("SongId", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", p1)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragment.arguments = args
            SongPlayingFragment.Statified.back = "Favorite"
            SongPlayingFragment.Statified.counter = 0
            if (SongPlayingFragment.Statified.mediaplayer != null && SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaplayer?.pause()
                SongPlayingFragment.Statified.mediaplayer?.release()
            }

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {

        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, p0, false)

        return MyViewHolder(itemView)

    }


    override fun getItemCount(): Int {

        return if (songDetails == null)
            0
        else
<<<<<<< HEAD
            (songDetails as ArrayList<songs>).size
=======
            return (songDetails as ArrayList<Songs>).size
>>>>>>> master
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackArtist = view.findViewById(R.id.trackArtist)
            trackTitle = view.findViewById(R.id.tracktitle)
            contentHolder = view.findViewById(R.id.contentRow)

        }

    }


}