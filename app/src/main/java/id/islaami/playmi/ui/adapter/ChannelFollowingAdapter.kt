package id.islaami.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.channel.Channel
import id.islaami.playmi.ui.channel.ChannelDetailActivity
import id.islaami.playmi.util.value
import id.islaami.playmi.util.ui.loadImage
import kotlinx.android.synthetic.main.channel_following_item.view.*

class ChannelFollowingAdapter(
    private var list: List<Channel> = emptyList(),
    var popMenu: (Context, View, Channel) -> Unit
) : RecyclerView.Adapter<ChannelFollowingAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_following_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun add(list: List<Channel>) {
        this.list = list
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(channel: Channel) = with(itemView) {
            channelThumb.loadImage(channel.thumbnail)
            channelName.text = channel.name

            itemLayout.setOnClickListener {
                ChannelDetailActivity.startActivity(
                    context,
                    channel.name.toString(),
                    channel.ID.value()
                )
            }

            menu.setOnClickListener { view ->
                popMenu(context, view, channel)
            }
        }
    }
}