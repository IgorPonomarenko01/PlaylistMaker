package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val trackNameView: TextView = itemView.findViewById(R.id.trackNameText)
        val artistNameView: TextView = itemView.findViewById(R.id.artistNameText)
        private val trackTimeView: TextView = itemView.findViewById(R.id.trackTimeText)
        private val artworkUrl100View: ImageView = itemView.findViewById(R.id.artworkImage)

        fun bind(model: Track) {
            trackNameView.text = model.trackName
            artistNameView.text = model.artistName
            trackTimeView.text = model.trackTime
            Glide.with(itemView.context)
                .load(model.artworkUrl100)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(artworkUrl100View)
        }
    }
}