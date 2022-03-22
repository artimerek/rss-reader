package pl.artimerek.xml_reader.adapter

import android.view.View
import android.widget.TextView
import pl.artimerek.xml_reader.R

class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvSummary: TextView = v.findViewById(R.id.tvSummary)
}