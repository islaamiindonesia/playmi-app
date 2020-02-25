package com.example.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.channel.ChannelDetailActivity
import com.example.playmi.ui.video.VideoDetailActivity
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.loadImage
import kotlinx.android.synthetic.main.video_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class VideoPagedAdapter(
    var context: Context? = null,
    var popMenuItemOnClickListener: (item: MenuItem, video: Video) -> Boolean
) :
    PagedListAdapter<Video, VideoPagedAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    fun addVideoList(list: PagedList<Video>?) {
        submitList(list)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video?) = with(itemView) {
            if (video != null) {
                videoTitle.text = video.title
                videoThumbnail.loadImage(video.thumbnail)
                channelName.text = video.channel
                channelIcon.loadImage(video.channelThumbnail)
                views.apply {
                    val views = video.views.toString()
                    text = if (views.isNullOrEmpty() || views == "0") {
                        "Belum ditonton"
                    } else {
                        views
                    }
                }
                subcategoryName.apply {
                    text = video.subcategory

                    setOnClickListener {

                    }
                }
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

                menu.setOnClickListener {
                    PopupMenu(context, it).apply {
                        inflate(R.menu.menu_popup_home)

                        if (video.followStatus != true) {
                            menu.getItem(1).title = "Mulai Mengikuti"
                        } else {
                            menu.getItem(1).title = "Berhenti Mengikuti"
                        }

                        // if already follow channel then startFollow -> stopFollow
                        setOnMenuItemClickListener { item ->
                            popMenuItemOnClickListener(item, video)
                        }

                        show()
                    }
                }

                channelLayout.setOnClickListener {
                    ChannelDetailActivity.startActivity(context, video.channel.toString(), video.channelID.value())
                }
            }
        }

        private fun differenceInDays(datePublished: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val videoDate = dateFormat.parse(datePublished)
            val today = Date()

            return (today.time - videoDate.time) / (1000 * 3600 * 24)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Video> =
            object : DiffUtil.ItemCallback<Video>() {
                override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem.ID === newItem.ID

                override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem == newItem
            }
    }
}
