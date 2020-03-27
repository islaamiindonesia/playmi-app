package id.islaami.playmi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.playlist.Playlist
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.playlist_sheet_item.view.*

class PlaylistSelectAdapter(
    var playList: List<Playlist>? = emptyList()
) :
    RecyclerView.Adapter<PlaylistSelectAdapter.ViewHolder>() {

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = -1

    override fun getItemCount(): Int = playList?.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_sheet_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        playList?.get(position)?.let { holder.bind(it, position) }
    }

    fun resetSelection() {
        checkedPosition = -1
    }

    fun getSelectedId(): Int {
        return if (checkedPosition != -1) {
            playList?.get(checkedPosition)?.ID.value()
        } else {
            0
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playList: Playlist, position: Int) = with(itemView) {
            radio.apply {
                isChecked = checkedPosition == position

                text = playList.name.toString()

                setOnClickListener {
                    checkedPosition = adapterPosition
                    notifyDataSetChanged()
                }
            }
        }
    }
}