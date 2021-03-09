package id.islaami.playmi2021.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.playlist.Playlist
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.playlist_sheet_item.view.*

class PlaylistSelectAdapter(
    var playList: List<Playlist>? = emptyList()
) : RecyclerView.Adapter<PlaylistSelectAdapter.ViewHolder>() {

    var selectedIds = ArrayList<Int>()

    override fun getItemCount(): Int = playList?.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_sheet_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        playList?.get(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playList: Playlist) = with(itemView) {
            radio.text = playList.name.toString()

            radio.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedIds.add(playList.ID.value())
                } else {
                    selectedIds.remove(playList.ID.value())
                }
            }
        }
    }
}