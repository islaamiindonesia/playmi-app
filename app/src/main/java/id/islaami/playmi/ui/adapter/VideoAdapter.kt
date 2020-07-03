package id.islaami.playmi.ui.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.video.VideoDetailActivity
import id.islaami.playmi.util.fromDbFormatDateTimeToCustomFormat
import id.islaami.playmi.util.ui.loadExternalImage
import id.islaami.playmi.util.ui.loadImage
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.value
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
            videoThumbnail.loadExternalImage(video.thumbnail)
            channelName.text = video.channel?.name
            channelIcon.loadImage(video.channel?.thumbnail)
            views.text = "${video.views ?: 0}x"

            if (video.isUploadShown == false) {
                layoutUploadTime.setVisibilityToGone()
            }

            subcategoryName.text = video.subcategory?.name

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
        }
    }
}