package com.example.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playmi.R
import com.example.playmi.data.model.playlist.Playlist
import com.example.playmi.ui.playlist.PlaylistVideoActivity
import com.example.playmi.util.value
import kotlinx.android.synthetic.main.playlist_item.view.*

class PlaylistAdapter(
    var playList: List<Playlist>? = emptyList(),
    var popMenu: (Context, View, Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun getItemCount(): Int = playList?.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        playList?.get(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playList: Playlist) = with(itemView) {
            playlistName.text = playList.name
            videoAmount.text = "${playList.videoCount.toString()} video"
            itemLayout.setOnClickListener {
                PlaylistVideoActivity.startActivity(context, playList.ID.value())
            }

            menu.setOnClickListener { view ->
                popMenu(context, view, playList)
            }
        }
    }
}