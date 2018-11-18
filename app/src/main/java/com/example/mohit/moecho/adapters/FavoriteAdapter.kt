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
import com.example.mohit.moecho.songs

class FavoriteAdapter(_songdetails: ArrayList<songs>, _context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songdetails
        this.mContext = _context
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val songObject = songDetails?.get(p1)
        p0.trackTitle?.text = songObject?.songTitle
        p0.trackArtist?.text = songObject?.artist
        p0.contentHolder?.setOnClickListener({
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist",songObject?.artist)
            args.putString("path",songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("SongId",songObject?.songID?.toInt() as Int)
            args.putInt("songPosition",p1)
            args.putParcelableArrayList("songData",songDetails)
            songPlayingFragment.arguments = args

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongPlayingFragmentFavorite")
                .commit()

        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {

        val itemView = LayoutInflater.from(p0?.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, p0, false)

        return MyViewHolder(itemView)

    }


    override fun getItemCount(): Int {

        if (songDetails == null)
            return 0
        else
            return (songDetails as ArrayList<songs>).size
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackArtist = view.findViewById<TextView>(R.id.trackArtist)
            trackTitle = view.findViewById<TextView>(R.id.tracktitle)
            contentHolder = view.findViewById<RelativeLayout>(R.id.contentRow)

        }

    }


}