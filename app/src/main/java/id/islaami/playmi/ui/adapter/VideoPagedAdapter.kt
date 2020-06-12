package id.islaami.playmi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.channel.ChannelDetailActivity
import id.islaami.playmi.ui.video.VideoDetailActivity
import id.islaami.playmi.util.ui.loadExternalImage
import id.islaami.playmi.util.ui.loadImage
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.ui.setVisibilityToVisible
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.video_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class VideoPagedAdapter(
    var context: Context? = null,
    var popMenu: (Context, View, Video) -> Unit
) : PagedListAdapter<Video, VideoPagedAdapter.ViewHolder>(DIFF_CALLBACK) {

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
                videoThumbnail.loadExternalImage(video.thumbnail)
                channelName.text = video.channel?.name
                channelIcon.loadImage(video.channel?.thumbnail)
                views.text = "${video.views ?: 0}x"

                subcategoryName.text = video.subcategory?.name

                if (video.isUploadShown == false) {
                    layoutUploadTime.setVisibilityToGone()
                    dot.setVisibilityToGone()
                } else {
                    layoutUploadTime.setVisibilityToVisible()
                    dot.setVisibilityToVisible()
                }

                recyclerView.adapter =
                    LabelAdapter(video.labels?.map { it.name.toString() } ?: emptyList())
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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

                channelLayout.setOnClickListener {
                    ChannelDetailActivity.startActivity(
                        context,
                        video.channel.toString(),
                        video.channel?.ID.value()
                    )
                }
            }
        }

        private fun differenceInDays(datePublished: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id"))
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
