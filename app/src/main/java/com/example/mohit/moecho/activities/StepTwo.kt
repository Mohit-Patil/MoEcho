package com.example.mohit.moecho.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mohit.moecho.R
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import java.io.IOException

class StepTwo : AppCompatActivity() {
    var searchbutton: Button? = null
    var searchquery: EditText? = null
    internal var tosearch: String? = null
    internal var videoid: String? = null
    var qq: String? = null

    object Statified {
        var search: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steptwo)
        searchbutton = findViewById<Button>(R.id.searchbuttonsong)
        searchquery = findViewById(R.id.searchquery)
        searchbutton?.setOnClickListener {
            var searchquery1 = searchquery?.getText().toString()
            Statified.search = searchquery1
            if (searchquery1.isEmpty()) {
                searchquery?.setError("Song Name is required")
                searchquery?.requestFocus()
            } else {
                val manager = applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = manager.activeNetworkInfo
                if (null != activeNetwork) {
                    if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        qq = ytsearch(searchquery1)
                        var clickIntent = Intent(this, PlayerActivity::class.java)
                        clickIntent.putExtra("videoid", qq)
                        startActivity(clickIntent)
                        System.out.println("Has Connection")
                    }
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    internal fun ytsearch(queryTerm: String): String {
        // Read the developer key from the properties file.
        val youtube: YouTube
        val HTTP_TRANSPORT = NetHttpTransport()
        val JSON_FACTORY = JacksonFactory()
        val NUMBER_OF_VIDEOS_RETURNED: Long = 1

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                HttpRequestInitializer { }).setApplicationName("youtube-cmdline-search-sample").build()

            // Prompt the user to enter a query term.


            // Define the API request for retrieving search results.
            val search = youtube.search().list("id,snippet")

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }
            val apiKey = "AIzaSyAecm5XasJhbVEXzp_XHZ8_R3p166R186Y"
            search.key = apiKey
            search.q = queryTerm

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.type = "video"

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.fields = "items(id/videoId,snippet/title,snippet/thumbnails/default/url)"
            search.maxResults = NUMBER_OF_VIDEOS_RETURNED

            // Call the API and print results.
            class PrimeThread : Thread() {


                override fun run() {
                    try {
                        val searchResponse = search.execute()
                        val searchResultList = searchResponse.items
                        if (searchResultList != null) {
                            videoid = prettyPrint(searchResultList!!.iterator(), queryTerm)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }

            val p = PrimeThread()
            p.start()


        } catch (e: GoogleJsonResponseException) {
            System.err.println(
                "There was a service error: " + e.details.code + " : "
                        + e.details.message
            )
        } catch (e: IOException) {
            System.err.println("There was an IO error: " + e.cause + " : " + e.message)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return "Qwerty"
    }

    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */

    internal fun prettyPrint(iteratorSearchResults: Iterator<SearchResult>, query: String): String {

        //System.out.println("\n=============================================================");
        //System.out.println(
        //    "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        //System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            println(" There aren't any results for your query.")
        }

        while (iteratorSearchResults.hasNext()) {

            val singleVideo = iteratorSearchResults.next()
            val rId = singleVideo.id
            qq = rId.videoId

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
        }
        return "Despacito"
    }
}