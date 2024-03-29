package id.islaami.playmi2021.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.channel.Channel
import id.islaami.playmi2021.ui.channel.ChannelDetailActivity
import id.islaami.playmi2021.util.value
import id.islaami.playmi2021.util.ui.loadImage
import kotlinx.android.synthetic.main.channel_following_item.view.*
import kotlinx.android.synthetic.main.channel_following_item.view.channelName
import kotlinx.android.synthetic.main.channel_following_item.view.channelThumb
import kotlinx.android.synthetic.main.channel_following_item.view.itemLayout
import kotlinx.android.synthetic.main.channel_following_item.view.verified_icon
import kotlinx.android.synthetic.main.channel_hidden_item.view.*

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
            verified_icon.isVisible = channel.status == 1

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