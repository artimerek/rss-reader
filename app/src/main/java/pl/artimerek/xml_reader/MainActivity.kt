package pl.artimerek.xml_reader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import pl.artimerek.xml_reader.parse.Parser
import java.net.URL
import kotlin.properties.Delegates
import kotlinx.android.synthetic.main.activity_main.*
import pl.artimerek.xml_reader.adapter.FeedAdapter
import pl.artimerek.xml_reader.urls.RssUrl

class MainActivity : AppCompatActivity() {

    private var downloadData: DownloadData? = null
    private var feedLimit = 10
    private var feedUrl: String = ""

    private var feedCachedUrl = "INVALIDATED"
    private val STATE_URL = "feedURL"
    private val STATE_LIMIT = "feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }

        downloadUrl(RssUrl.SONGS.type.format(feedLimit))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuSongs ->
                feedUrl = RssUrl.SONGS.type
            R.id.menuPaid ->
                feedUrl = RssUrl.PAID.type
            R.id.menuFree ->
                feedUrl = RssUrl.FREE.type
            R.id.menu10, R.id.menu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                }
            }
            R.id.menuRefresh -> feedCachedUrl = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    private fun downloadUrl(feedUrl: String) {
        if (feedUrl != feedCachedUrl) {
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)
            feedCachedUrl = feedUrl
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        return true
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTask<String, Void, String>() {

            private var propContext: Context by Delegates.notNull()
            private var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun doInBackground(vararg url: String?): String {
                return downloadXML(url[0])
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