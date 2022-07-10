package com.example.mohit.moecho.resources

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
<<<<<<<< HEAD:app/src/main/java/com/example/mohit/moecho/resources/songs.kt
class songs(var songID: Long, var songTitle: String, var artist: String, var songData: String, var dateAdded: Long) :
========
class Songs(var songID: Long, var songTitle: String, var artist: String, var songData: String, var dateAdded: Long) :
>>>>>>>> master:app/src/main/java/com/example/mohit/moecho/Songs.kt
    Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    object Statified {
<<<<<<<< HEAD:app/src/main/java/com/example/mohit/moecho/resources/songs.kt
        var nameComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
========
        var nameComparator: Comparator<Songs> = Comparator { song1, song2 ->
>>>>>>>> master:app/src/main/java/com/example/mohit/moecho/Songs.kt
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)

        }
<<<<<<<< HEAD:app/src/main/java/com/example/mohit/moecho/resources/songs.kt
        var dateComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
========
        var dateComparator: Comparator<Songs> = Comparator { song1, song2 ->
>>>>>>>> master:app/src/main/java/com/example/mohit/moecho/Songs.kt
            val songOne = song1.dateAdded.toDouble()
            val songTwo = song2.dateAdded.toDouble()
            songTwo.compareTo(songOne)
        }
    }


}