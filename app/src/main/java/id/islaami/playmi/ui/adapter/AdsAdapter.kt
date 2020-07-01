package id.islaami.playmi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.NativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import kotlinx.android.synthetic.main.video_item.view.*

class AdsAdapter(var list: List<Any> = emptyList()) :
    PagedListAdapter<Any, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Any> =
            object : DiffUtil.ItemCallback<Any>() {
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
                    when {
                        oldItem is Video && newItem is Video -> oldItem.ID == newItem.ID
                        else -> true
                    }

                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
                    when {
                        oldItem is Video && newItem is Video -> oldItem == newItem
                        else -> true
                    }
            }
    }

    // A menu item view type.
    private val MENU_ITEM_VIEW_TYPE = 0

    // The unified native ad view type.
    private val UNIFIED_NATIVE_AD_VIEW_TYPE = 1

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = list[position]
        return if (recyclerViewItem is UnifiedNativeAd) {
            UNIFIED_NATIVE_AD_VIEW_TYPE
        } else MENU_ITEM_VIEW_TYPE
    }

    inner class AdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video) = with(itemView) {
            videoTitle.text = video.title
        }
    }

    fun addList(list: PagedList<Any>?) {
        submitList(list)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView: View = LayoutInflater.from(
                    viewGroup.context
                ).inflate(
                    R.layout.ad_unified,
                    viewGroup, false
                )
                UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
            }
            else -> {
                val menuItemLayoutView: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.video_item, viewGroup, false)
                AdsViewHolder(menuItemLayoutView)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd =
                    list[position] as UnifiedNativeAd

                populateNativeAdView(nativeAd, (holder as UnifiedNativeAdViewHolder).adView)
            }
            else -> {
                val videoHolder = holder as AdsViewHolder

                videoHolder.bind(list[position] as Video)
            }
        }
    }

    private fun populateNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        val icon: NativeAd.Image? = nativeAd.icon
        if (icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }
}