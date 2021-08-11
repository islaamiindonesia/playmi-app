package id.islaami.playmi2021.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import kotlinx.android.synthetic.main.playlist_detail_header_item.view.*

class PlaylistDetailHeaderAdapter: RecyclerView.Adapter<PlaylistDetailHeaderAdapter.HeaderViewHolder>() {
    private var playlistName: String = ""
    private var videoCount: Int = 0
    private var onClickListener: PlaylistDetailClickListener? = null

    fun setOnClickListener(onClickListener: PlaylistDetailClickListener) {
        this.onClickListener = onClickListener
    }

    fun setData(playlistName: String?, videoCount: Int?) {
        this.playlistName = playlistName ?: ""
        this.videoCount = videoCount ?: 0
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String, count: Int) {
            with(itemView) {
                playlistName.text = name
                videoAmount.text = "$count video"
                btnEdit.setOnClickListener {
                    onClickListener?.onEditPlaylistNameClicked(name)
                }
                btnDelete.setOnClickListener {
                    onClickListener?.onDeletePlaylistClicked()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_detail_header_item, parent, false))
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(playlistName, videoCount)
    }

    override fun getItemCount(): Int = 1

    interface PlaylistDetailClickListener {
        fun onEditPlaylistNameClicked(playlistName: String)
        fun onDeletePlaylistClicked()
    }
}