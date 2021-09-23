package id.islaami.playmi2021.ui.adapter

import android.view.View

interface PlaybackViewHolder {
    val currentPosition: Int
    val playerView: View
    fun playVideo()
    fun pauseVideo()
    val isPlaying: Boolean
}