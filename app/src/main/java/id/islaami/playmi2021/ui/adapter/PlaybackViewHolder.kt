package id.islaami.playmi2021.ui.adapter

interface PlaybackViewHolder {
    val currentPosition: Int
    val isVisible: Boolean
    fun playVideo()
    fun pauseVideo()
}