package id.islaami.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.playlist.Playlist
import id.islaami.playmi.ui.playlist.PlaylistDetailActivity
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.playlist_item.view.*

class PlaylistAdapter(
    private var list: List<Playlist> = emptyList(),
    var popMenu: (Context, View, Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun add(list: List<Playlist>) {
        this.list = list
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playList: Playlist) = with(itemView) {
            playlistName.text = playList.name
            videoAmount.text = "${playList.videoCount.toString()} video"
            itemLayout.setOnClickListener {
                PlaylistDetailActivity.startActivity(context, playList.ID.value(), playList.name.toString())
            }

            menu.setOnClickListener { view ->
                popMenu(context, view, playList)
            }
        }
    }
}