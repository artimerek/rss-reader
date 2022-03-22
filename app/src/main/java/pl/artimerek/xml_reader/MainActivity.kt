package pl.artimerek.xml_reader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import pl.artimerek.xml_reader.parse.Parser
import java.net.URL
import kotlin.properties.Delegates
import kotlinx.android.synthetic.main.activity_main.*
import pl.artimerek.xml_reader.adapter.FeedAdapter

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val downloadData by lazy { DownloadData(this, xmlListView) }
    private val URL_TOP_TEN =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreateCalled")
        downloadData.execute(URL_TOP_TEN)
        Log.d(TAG, "onCreate done")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) : AsyncTask<String, Void, String>() {

            private val TAG = "DownloadData"

            private var propContext: Context by Delegates.notNull()
            private var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground called ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parser = Parser()
                parser.parse(result)

                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parser.apps)
                propListView.adapter = feedAdapter
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }
}