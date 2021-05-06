package id.islaami.playmi2021.ui.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.ui.channel.ChannelDetailActivity
import id.islaami.playmi2021.ui.video.VideoDetailActivity
import id.islaami.playmi2021.ui.video.VideoSeriesActivity
import id.islaami.playmi2021.ui.video.VideoSubcategoryActivity
import id.islaami.playmi2021.util.fromDbFormatDateTimeToCustomFormat
import id.islaami.playmi2021.util.ui.loadImage
import id.islaami.playmi2021.util.ui.loadYoutubeThumbnail
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.video_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class VideoAdapter(
    private var list: List<Video> = emptyList(),
    var popMenu: (Context, View, Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun add(list: List<Video>) {
        this.list = list
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video) = with(itemView) {
            videoTitle.text = video.title
            videoThumbnail.loadYoutubeThumbnail(video.videoID ?: "")
            channelName.text = video.channel?.name
            channelIcon.loadImage(video.channel?.thumbnail)
            verified_icon.isVisible = video.channel?.status == 1
            views.text = "${video.views ?: 0}x"
            serial.isVisible = video.seriesId != null
            serial_name.text = video.seriesName
            serial_name.setOnClickListener {
                VideoSeriesActivity.startActivity(context, video.seriesId.value(), video.seriesName.toString())
            }

            subcategoryName.apply {
                text = video.subcategory?.name
                setOnClickListener {
                    VideoSubcategoryActivity.startActivity(
                        context,
                        video.category?.ID.value(),
                        video.subcategory?.ID.value(),
                        video.subcategory?.name.toString()
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val locale = Locale.forLanguageTag("id")
                val message = TimeAgoMessages.Builder().withLocale(locale).build()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id"))
                val videoDate = dateFormat.parse(video.publishedAt.toString())

                publishedDate.text = TimeAgo.using(videoDate?.time.value(), message)
            } else {
                publishedDate.text =
                    video.publishedAt.fromDbFormatDateTimeToCustomFormat("dd MM yyyy")
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
}