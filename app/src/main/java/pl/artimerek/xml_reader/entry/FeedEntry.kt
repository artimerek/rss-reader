package pl.artimerek.xml_reader.entry

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return "name='$name', artist='$artist', " + "releaseDate='$releaseDate', " +
                "summary='$summary'," + " imageURL='$imageURL')"
    }
}