package com.example.playmi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playmi.R
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.ui.channel.ChannelDetailActivity
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.loadImage
import kotlinx.android.synthetic.main.channel_hidden_item.view.*

class ChannelHiddenAdapter(
    var list: List<Channel>? = emptyList(),
    var btnListener: (Int) -> Unit
) :
    RecyclerView.Adapter<ChannelHiddenAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list?.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_hidden_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position)?.let { holder.bind(it) }
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

            btnShow.setOnClickListener { btnListener(channel.ID.value()) }
        }
    }
}