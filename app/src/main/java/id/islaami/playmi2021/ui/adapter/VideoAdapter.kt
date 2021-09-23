package id.islaami.playmi2021.ui.adapter

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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
    val context: Context,
    private var list: List<Video> = emptyList(),
    var popMenu: (Context, View, Video) -> Unit,
    val onPlaybackEnded: ((playedPosition: Int) -> Unit)? = null,
    val onVideoWatched10Seconds: ((Int) -> Unit)? = null,
    val lifecycle: Lifecycle
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    var currentPlayedView: PlaybackViewHolder? = null
    private val videoPlayer = YouTubePlayerView(context).apply {
        lifecycle.addObserver(this)
        this.enableBackgroundPlayback(false)
        this.getPlayerUiController()
            .showSeekBar(false)
            .showFullscreenButton(false)
            .showCurrentTime(false)
            .showDuration(false)
            .showYouTubeButton(false)
            .showVideoTitle(false)
            .showMenuButton(false)
            .showPlayPauseButton(false)
    }

    override fun getItemCount(): Int = list.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    fun add(list: List<Video>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PlaybackViewHolder {

        private var duration = 0
        private var onWatched = false // to lock the function invoke, prevent multiple call
        private var onAutoPlayNextCalled = false // to lock the function invoke, prevent multiple call
        private var _currentPosition = 0
        private var isVideoPlaying = false

        private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                super.onVideoDuration(youTubePlayer, duration)
                this@ViewHolder.duration = duration.toInt()
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                if (second != 0f && duration != 0) {
                    if (second.toInt() == duration && !onAutoPlayNextCalled) {
                        onPlaybackEnded?.invoke(currentPlayedView?.currentPosition ?: 0)
                        onAutoPlayNextCalled = true
                    }
                    if (second > 10 && !onWatched) {
                        val video = list[currentPlayedView?.currentPosition ?: 0]
                        onVideoWatched10Seconds?.invoke(video.ID ?: 0)
                        onWatched = true
                    }
                }
            }
        }

        fun bind(video: Video, position: Int) = with(itemView) {
            _currentPosition = position
            if (position == 0) {
                currentPlayedView = this@ViewHolder
            }
            duration = 0
            onWatched = false
            videoTitle.text = video.title
            videoThumbnail.loadYoutubeThumbnail(video.videoID ?: "")
            videoThumbnail.visibility = View.VISIBLE
            if (position == 0) {
                isVideoPlaying = true
                playVideo()
            }

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
            videoPlayerTopLayer.setOnClickListener {
                VideoDetailActivity.startActivity(context, video.ID.value())
            }
            videoTitle.setOnClickListener {
                videoThumbnail.callOnClick()
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

        override val currentPosition: Int
            get() = _currentPosition
        override val playerView: View
            get() = itemView.videoPlayerTopLayer

        override val isPlaying: Boolean
            get() = isVideoPlaying

        override fun playVideo() {
            currentPlayedView?.pauseVideo()
            this@VideoAdapter.handler.postDelayed({
                with(itemView) {
                    videoPlayer.addYouTubePlayerListener(youTubePlayerListener)
                    videoThumbnail.visibility = View.INVISIBLE
                    videoPlayerContainer.visibility = View.VISIBLE
                    if (videoPlayer.parent == null) {
                        videoPlayerContainer.addView(videoPlayer)
                    }
                    onAutoPlayNextCalled = false
                    videoPlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadOrCueVideo(lifecycle, list[_currentPosition].videoID.toString(), 0f)
                        }
                    })
                    currentPlayedView = this@ViewHolder
                    isVideoPlaying = true
                }
            }, 2300)
        }
        override fun pauseVideo() {
            if (videoPlayer.parent != null) {
                (videoPlayer.parent as ViewGroup).removeView(videoPlayer)
            }
            with(itemView) {
                videoPlayer.removeYouTubePlayerListener(youTubePlayerListener)
                videoThumbnail.visibility = View.VISIBLE
                videoPlayerContainer.visibility = View.INVISIBLE
                this@VideoAdapter.handler.removeCallbacksAndMessages(null)
                videoPlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.pause()
                    }
                })
                isVideoPlaying = false
            }
        }
    }
}