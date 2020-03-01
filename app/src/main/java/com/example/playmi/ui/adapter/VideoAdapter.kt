package com.example.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.video.VideoDetailActivity
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.loadImage
import kotlinx.android.synthetic.main.video_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class VideoAdapter(
    var list: List<Video>? = emptyList(),
    var popMenu: (Context, View, Video) -> Unit
) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list?.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video) = with(itemView) {
            videoTitle.text = video.title
            videoThumbnail.loadImage(video.thumbnail)
            channelName.text = video.channel
            channelIcon.loadImage(video.channelThumbnail)
            views.apply {
                val views = video.views.toString()
                text = if (views.isEmpty() || views == "0") {
                    "Belum ditonton"
                } else {
                    views
                }
            }
            subcategoryName.text = video.subcategory
            publishedDate.apply {
                val time = differenceInDays(video.publishedAt.toString())
                text = if (time == 0L) {
                    "Hari ini"
                } else {
                    "$time hari yang lalu"
                }
            }

            videoThumbnail.setOnClickListener {
                VideoDetailActivity.startActivity(context, video.ID.value())
            }

            menu.setOnClickListener { view ->
                popMenu(context, view, video)
            }
        }

        private fun differenceInDays(datePublished: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val videoDate = dateFormat.parse(datePublished)
            val today = Date()

            val difference = (today.time - videoDate.time) / (1000 * 3600 * 24)

            return difference
        }
    }
}