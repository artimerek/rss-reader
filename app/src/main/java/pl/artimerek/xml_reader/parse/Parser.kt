package pl.artimerek.xml_reader.parse

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import pl.artimerek.xml_reader.entry.FeedEntry
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class Parser {

    val apps = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true

            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())

            var eventType = xpp.eventType
            var currentRecord = FeedEntry()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.lowercase(Locale.getDefault())
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    apps.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()
                                }

                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releaseDate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> currentRecord.imageURL = textValue
                            }
                        }
                    }
                }

                eventType = xpp.next()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }

        return status
    }
}