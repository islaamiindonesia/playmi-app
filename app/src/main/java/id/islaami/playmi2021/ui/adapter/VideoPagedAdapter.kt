package id.islaami.playmi2021.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
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
import java.text.SimpleDateFormat
import java.util.*

class VideoPagedAdapter(
    var context: Context? = null,
    var popMenu: (Context, View, Video) -> Unit,
    var showSeries: Boolean = true,
) : PagedListAdapter<Video, VideoPagedAdapter.ViewHolder>(DIFF_CALLBACK) {

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
        holder.bindView(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.title == "ads") UNIFIED_NATIVE_AD_VIEW_TYPE
        else VIDEO_ITEM_VIEW_TYPE
    }

    fun addVideoList(list: PagedList<Video>?) {
        submitList(list)
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(data: Video?)
    }

    inner class VideoViewHolder(itemView: View) : ViewHolder(itemView) {

        override fun bindView(video: Video?) = with(itemView) {
            if (video != null) {
                videoTitle.text = video.title
                videoThumbnail.loadYoutubeThumbnail(video.videoID ?: "")
                channelName.text = video.channel?.name
                channelIcon.loadImage(video.channel?.thumbnail)
                views.text = "${video.views ?: 0}x"
                verified_icon.isVisible = video.channel?.status == 1
                serial.isVisible = video.seriesId != null && showSeries
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
                        channelName = video.channel?.name.toString(),
                        channelID = video.channel?.ID.value()
                    )
                }
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
        override fun bindView(data: Video?) {
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
            if(!unifiedNativeAd.mediaContent.hasVideoContent()) ad_media?.isVisible = false

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
        override fun bindView(data: Video?) {
            TODO("Not yet implemented")
        }
    }
}
