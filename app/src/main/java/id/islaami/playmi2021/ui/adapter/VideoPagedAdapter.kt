package id.islaami.playmi2021.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.ui.channel.ChannelDetailActivity
import id.islaami.playmi2021.ui.video.VideoDetailActivity
import id.islaami.playmi2021.ui.video.VideoSeriesActivity
import id.islaami.playmi2021.ui.video.VideoSubcategoryActivity
import id.islaami.playmi2021.util.fromDbFormatDateTimeToCustomFormat
import id.islaami.playmi2021.util.ui.*
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.video_item.view.*
import kotlinx.android.synthetic.main.video_update_fragment.*
import java.text.SimpleDateFormat
import java.util.*


class VideoPagedAdapter(
    var context: Context? = null,
    var popMenu: (Context, View, Video) -> Unit,
    var showSeries: Boolean = true,
    val onPlaybackEnded: ((playedPosition: Int) -> Unit)? = null,
    val onVideoWatched10Seconds: ((Int) -> Unit)? = null,
    val lifecycle: Lifecycle,
    var autoPlayOnLoad: Boolean = false
) : PagedListAdapter<Video, VideoPagedAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val handler = Handler(Looper.getMainLooper())
    var currentPlayedView: PlaybackViewHolder? = null

    companion object {
        const val VIDEO_ITEM_VIEW_TYPE = 0
        const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Video> =
            object : DiffUtil.ItemCallback<Video>() {
                override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem.ID === newItem.ID

                override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIDEO_ITEM_VIEW_TYPE -> VideoViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
            )
            UNIFIED_NATIVE_AD_VIEW_TYPE -> AdViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.ad_unified, parent, false)
            )
            else -> EmptyDataPlaceHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*if (holder is AdViewHolder)
            Log.i("Cek ", "Ad at position: $position")*/
        holder.bindView(getItem(position), position)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is PlaybackViewHolder) {
            holder.pauseVideo()
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.title == "ads") UNIFIED_NATIVE_AD_VIEW_TYPE
        else VIDEO_ITEM_VIEW_TYPE
    }

    fun addVideoList(list: PagedList<Video>?) {
        submitList(list)
        notifyDataSetChanged()
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(data: Video?, position: Int)
    }

    inner class VideoViewHolder(itemView: View) : ViewHolder(itemView), PlaybackViewHolder {
        private var duration = 0
        private var onWatched = false // to lock the function invoke, prevent multiple call
        private var onAutoPlayNextCalled =
            false // to lock the function invoke, prevent multiple call
        private var _currentPosition = 0
        private var isVideoPlaying = false

        private val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                super.onVideoDuration(youTubePlayer, duration)
                this@VideoViewHolder.duration = duration.toInt()
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)

                if (second != 0f && duration != 0) {
                    if (second.toInt() == duration && !onAutoPlayNextCalled) {
                        onPlaybackEnded?.invoke(currentPlayedView?.currentPosition ?: 0)
                        onAutoPlayNextCalled = true
                    }
                    if (second > 10 && !onWatched) {
                        val video = getItem(currentPlayedView?.currentPosition ?: 0)
                        onVideoWatched10Seconds?.invoke(video?.ID ?: 0)
                        onWatched = true
                    }
                }
            }
        }

        override fun bindView(video: Video?, position: Int) = with(itemView) {
            if (video != null) {
                _currentPosition = position
                if (position == 0) {
                    currentPlayedView = this@VideoViewHolder
                }
                duration = 0
                onWatched = false
                videoTitle.text = video.title
                videoThumbnail.loadYoutubeThumbnail(video.videoID ?: "")
                videoThumbnail.visibility = View.VISIBLE
                lifecycle.addObserver(videoPlayer)
                videoPlayer.visibility = View.INVISIBLE
                videoPlayer.enableBackgroundPlayback(false)
                videoPlayer.getPlayerUiController()
                    .showVideoTitle(false)
                    .showMenuButton(false)
                    .showPlayPauseButton(false)
                videoPlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(video.videoID.toString(), 0f)
                        if (isVideoPlaying) playVideo()
                    }
                })
                if (position == 0 && autoPlayOnLoad) {
                    isVideoPlaying = true
                    playVideo()
                }

                channelName.text = video.channel?.name
                channelIcon.loadImage(video.channel?.thumbnail)
                views.text = "${video.views ?: 0}x"
                verified_icon.isVisible = video.channel?.status == 1
                serial.isVisible = video.seriesId != null && showSeries
                serial_name.text = video.seriesName
                serial_name.setOnClickListener {
                    VideoSeriesActivity.startActivity(
                        context,
                        video.seriesId.value(),
                        video.seriesName.toString()
                    )
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
                        channelName = video.channel?.name.toString(),
                        channelID = video.channel?.ID.value()
                    )
                }
            }
        }

        override val currentPosition: Int
            get() = _currentPosition
        override val isVisible: Boolean
            get() = itemView.videoPlayerTopLayer.isVisible()

        override fun playVideo() {
            currentPlayedView?.pauseVideo()
            this@VideoPagedAdapter.handler.postDelayed({
                with(itemView) {
                    videoPlayer.addYouTubePlayerListener(youtubePlayerListener)
                    videoThumbnail.visibility = View.INVISIBLE
                    videoPlayer.visibility = View.VISIBLE
                    onAutoPlayNextCalled = false
                    videoPlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.seekTo(0f)
                            youTubePlayer.play()
                        }
                    })
                    currentPlayedView = this@VideoViewHolder
                    isVideoPlaying = true
                }
            }, 2300)
        }

        override fun pauseVideo() {
            with(itemView) {
                videoPlayer.removeYouTubePlayerListener(youtubePlayerListener)
                videoThumbnail.visibility = View.VISIBLE
                videoPlayer.visibility = View.INVISIBLE
                this@VideoPagedAdapter.handler.removeCallbacksAndMessages(null)
                videoPlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.pause()
                    }
                })
                isVideoPlaying = false
            }
        }
    }

    inner class AdViewHolder(itemView: View) : ViewHolder(itemView) {
        // do any ad setup

        private var unav: UnifiedNativeAdView? = null
        private var ad_card: ConstraintLayout? = null
        private var ad_app_icon: ImageView? = null
        private var ad_headline: TextView? = null
        private var ad_advertiser: TextView? = null
        private var ad_stars: RatingBar? = null
        private var ad_body: TextView? = null
        private var ad_media: MediaView? = null
        private var ad_price: TextView? = null
        private var ad_store: TextView? = null
        private var ad_call_to_action: Button? = null


        private var currentNativeAd: UnifiedNativeAd? = null

        init {
            unav = itemView.findViewById(R.id.unav)
            ad_card = itemView.findViewById(R.id.adCard)
            ad_app_icon = itemView.findViewById(R.id.ad_app_icon)
            ad_headline = itemView.findViewById(R.id.ad_headline)
            ad_advertiser = itemView.findViewById(R.id.ad_advertiser)
            ad_stars = itemView.findViewById(R.id.ad_stars)
            ad_body = itemView.findViewById(R.id.ad_body)
            ad_media = itemView.findViewById(R.id.ad_media)
            ad_price = itemView.findViewById(R.id.ad_price)
            ad_store = itemView.findViewById(R.id.ad_store)
            ad_call_to_action = itemView.findViewById(R.id.ad_call_to_action)
        }

        @SuppressLint("MissingPermission")
        override fun bindView(data: Video?, position: Int) {
            hideAdViews()
            currentNativeAd?.destroy()

            val adLoader = AdLoader.Builder(
                itemView.context,
                itemView.context.getString(R.string.ad_native_unit_id)
            ).forUnifiedNativeAd { ad ->
                currentNativeAd = ad

                unav?.let { populateAd(ad, it) }
            }.withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateAd(
            unifiedNativeAd: UnifiedNativeAd,
            unifiedNativeAdView: UnifiedNativeAdView
        ) {
            ad_card?.isVisible = true
            ad_headline?.text = unifiedNativeAd.headline
            ad_media?.setMediaContent(unifiedNativeAd.mediaContent)
            if (!unifiedNativeAd.mediaContent.hasVideoContent()) ad_media?.isVisible = false

            unifiedNativeAd.body?.let {
                ad_body?.visibility = View.VISIBLE
                ad_body?.text = it
            } ?: kotlin.run { ad_body?.visibility = View.INVISIBLE }

            unifiedNativeAd.callToAction?.let {
                ad_call_to_action?.visibility = View.VISIBLE
                ad_call_to_action?.text = it
            } ?: kotlin.run { ad_call_to_action?.visibility = View.INVISIBLE }

            unifiedNativeAd.icon?.let {
                ad_app_icon?.visibility = View.VISIBLE
                ad_app_icon?.setImageDrawable(it.drawable)
            } ?: kotlin.run { ad_app_icon?.visibility = View.INVISIBLE }

            unifiedNativeAd.price?.let {
                ad_price?.visibility = View.VISIBLE
                ad_price?.text = it
            } ?: kotlin.run { ad_price?.visibility = View.INVISIBLE }

            unifiedNativeAd.store?.let {
                ad_store?.visibility = View.VISIBLE
                ad_store?.text = it
            } ?: kotlin.run { ad_store?.visibility = View.INVISIBLE }

            unifiedNativeAd.starRating?.let {
                ad_stars?.visibility = View.VISIBLE
                ad_stars?.rating = it.toFloat()
            } ?: kotlin.run { ad_stars?.visibility = View.INVISIBLE }

            unifiedNativeAd.advertiser?.let {
                ad_advertiser?.visibility = View.VISIBLE
                ad_advertiser?.text = it
            } ?: kotlin.run { ad_advertiser?.visibility = View.INVISIBLE }

            unifiedNativeAdView.headlineView = ad_headline
            unifiedNativeAdView.bodyView = ad_body
            unifiedNativeAdView.callToActionView = ad_call_to_action
            unifiedNativeAdView.iconView = ad_app_icon
            unifiedNativeAdView.priceView = ad_price
            unifiedNativeAdView.starRatingView = ad_stars
            unifiedNativeAdView.storeView = ad_store
            unifiedNativeAdView.advertiserView = ad_advertiser
            unifiedNativeAdView.mediaView = ad_media

            unifiedNativeAdView.mediaView.setMediaContent(unifiedNativeAd.mediaContent)

            unifiedNativeAdView.setNativeAd(unifiedNativeAd)
        }

        private fun hideAdViews() {
            ad_card?.isVisible = false
            ad_app_icon?.isVisible = false
            ad_headline?.isVisible = false
            ad_advertiser?.isVisible = false
            ad_stars?.isVisible = false
            ad_body?.isVisible = false
            ad_media?.isVisible = false
            ad_price?.isVisible = false
            ad_store?.isVisible = false
            ad_call_to_action?.isVisible = false
        }
    }

    inner class EmptyDataPlaceHolder(itemView: View) : ViewHolder(itemView) {
        override fun bindView(data: Video?, position: Int) {
            TODO("Not yet implemented")
        }
    }
}
