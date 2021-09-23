package id.islaami.playmi2021.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder
import id.islaami.playmi2021.util.ui.isHalfBottomVisibleOn
import id.islaami.playmi2021.util.ui.isHalfTopVisibleOn

class AutoPlayScrollListener(
    var getCurrentPlayedViewHolder: () -> PlaybackViewHolder?,
) : RecyclerView.OnScrollListener() {

    private var isNext = true // false for previous

    private fun playNextOrPrevious(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val currentViewHolder = getCurrentPlayedViewHolder()
        val nextPlayedIndex: Int
        if (isNext) {
            if (currentViewHolder?.playerView.isHalfTopVisibleOn(recyclerView)) return
            layoutManager.findFirstCompletelyVisibleItemPosition()
            val firstVisibleItemPos = layoutManager.findFirstCompletelyVisibleItemPosition()
            nextPlayedIndex = if (recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPos) is PlaybackViewHolder) {
                firstVisibleItemPos
            } else {
                firstVisibleItemPos + 1
            }
        } else {
            if (currentViewHolder?.playerView.isHalfBottomVisibleOn(recyclerView)) return
            val lastVisibleItemPos = layoutManager.findLastCompletelyVisibleItemPosition()
            nextPlayedIndex = if (recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPos) is PlaybackViewHolder) {
                lastVisibleItemPos
            } else {
                lastVisibleItemPos - 1
            }
        }
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(nextPlayedIndex)
        if (viewHolder is PlaybackViewHolder) {
            viewHolder.playVideo()
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) { // scrolling down
            isNext = true
        } else if (dy < 0) { // scrolling up
            isNext = false
        }
        playNextOrPrevious(recyclerView)
    }
}