package com.example.mohit.moecho.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
<<<<<<< HEAD
import com.example.mohit.moecho.resources.songs
=======
import com.example.mohit.moecho.Songs
import java.lang.Exception
>>>>>>> master

class EchoDatabase : SQLiteOpenHelper {

    private var _songList = ArrayList<Songs>()


    object Staticated {
        var DB_VERSION = 1
        const val DB_NAME = "FavoriteDatabase"
        const val TABLE_NAME = "FavoriteTable"
        const val COLUMN_ID = "SongId"
        const val COLUMN_SONG_TITLE = "SongTitle"
        const val COLUMN_SONG_ARTIST = "SongArtist"
        const val COLUMN_SONG_PATH = "SongPath"
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(
        context,
        Staticated.DB_NAME,
        null,
        Staticated.DB_VERSION
    )

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
<<<<<<< HEAD
        sqLiteDatabase?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "(" + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " TEXT," + Staticated.COLUMN_SONG_TITLE + " TEXT," + Staticated.COLUMN_SONG_PATH + " TEXT);")
=======
        sqLiteDatabase?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "(" + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING," + Staticated.COLUMN_SONG_PATH + " STRING);")
>>>>>>> master


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun storeAsFavourite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Staticated.COLUMN_ID, id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentValues)
        db.close()
    }

    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val queryparams = "SELECT * FROM " + Staticated.TABLE_NAME
            val cSor = db.rawQuery(queryparams, null)
            if (cSor.moveToFirst()) {
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                } while (cSor.moveToNext())
            } else {
                db.close()
                return null
            }
            db.close()
            cSor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songList

    }

    fun checkifIdExists(_id: Int): Boolean {
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            } while (cSor.moveToNext())
        } else {
            db.close()
            return false
        }
        cSor.close()
        db.close()
        cSor.close()
        return storeId != -1090
    }

    fun deleteFavorite(_id: Int) {
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                counter += 1
            } while (cSor.moveToNext())
        } else {
            db.close()
            return 0
        }
        cSor.close()
        db.close()
        cSor.close()
        return counter

    }

}