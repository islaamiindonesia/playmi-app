package id.islaami.playmi2021.util

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder

class AutoPlayScrollListener(
    var getCurrentPlayedViewHolder: () -> PlaybackViewHolder?,
) : RecyclerView.OnScrollListener() {

    private var isNext = true // false for previous

    private fun playNextOrPrevious(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val nextPlayedIndex = if (isNext) {
            layoutManager.findFirstCompletelyVisibleItemPosition()
            val firstVisibleItemPos = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPos) is PlaybackViewHolder) {
                firstVisibleItemPos
            } else {
                firstVisibleItemPos + 1
            }
        } else {
            val lastVisibleItemPos = layoutManager.findLastCompletelyVisibleItemPosition()
            if (recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPos) is PlaybackViewHolder) {
                lastVisibleItemPos
            } else {
                lastVisibleItemPos - 1
            }
        }
        if (getCurrentPlayedViewHolder()?.currentPosition == nextPlayedIndex) return
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
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE, RecyclerView.SCROLL_STATE_DRAGGING -> {
                playNextOrPrevious(recyclerView)
            }
        }
    }
}