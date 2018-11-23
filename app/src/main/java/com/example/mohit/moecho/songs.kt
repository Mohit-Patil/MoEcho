package com.example.mohit.moecho

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

/*This is a custom data model used for saving the complete details of a song together. Every time in Android when we want to store a group of data together we create a model class
* This model class here is used to store the Songs. The information which we need for every song is its name(title), artist, data and the date on which that song was added in our device*/
@SuppressLint("ParcelCreator")
class songs(var songID: Long, var songTitle: String, var artist: String, var songData: String, var dateAdded: Long) :
    Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    object Statified {
        var nameComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)

        }
        var dateComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
            val songOne = song1.dateAdded.toDouble()
            val songTwo = song2.dateAdded.toDouble()
            songTwo.compareTo(songOne)
        }
    }


}