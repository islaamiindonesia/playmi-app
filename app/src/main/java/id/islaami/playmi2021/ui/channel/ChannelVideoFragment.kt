package id.islaami.playmi2021.ui.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder
import id.islaami.playmi2021.ui.adapter.VideoPagedAdapterOld
import id.islaami.playmi2021.ui.base.BaseFragment
import id.islaami.playmi2021.ui.base.BaseRecyclerViewFragment
import id.islaami.playmi2021.ui.video.VideoViewModel
import id.islaami.playmi2021.util.AutoPlayScrollListener
import id.islaami.playmi2021.util.ERROR_EMPTY_LIST
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.CustomLinearLayoutManager
import id.islaami.playmi2021.util.ui.PlaymiDialogFragment
import id.islaami.playmi2021.util.ui.showLongToast
import id.islaami.playmi2021.util.ui.showSnackbar
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.channel_video_fragment.recyclerView
import kotlinx.android.synthetic.main.video_update_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelVideoFragment(var channelID: Int) : BaseFragment(), BaseRecyclerViewFragment {
    val viewModel: ChannelViewModel by viewModel()
    private val videoViewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapterOld(context,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_channel_detail)

                menu.getItem(0).title = "Simpan ke Tonton Nanti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popSaveLater -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = childFragmentManager,
                                text = "Simpan ke daftar Tonton Nanti?",
                                okCallback = { viewModel.watchLater(video.ID.value()) }
                            )

                            true
                        }
                        else -> false
                    }
                }

                show()
            }
        },
        onPlaybackEnded = {
            var nextPosition = it
            val videoCount = recyclerView.adapter?.itemCount ?: 0
            while (nextPosition < videoCount) {
                if (recyclerView.findViewHolderForAdapterPosition(++nextPosition) is PlaybackViewHolder) {
                    val videoPlayedItem = recyclerView.layoutManager?.findViewByPosition(nextPosition)
                    val videoPlayedLoc = IntArray(2)
                    val videoView = videoPlayedItem?.findViewById<RelativeLayout>(R.id.channelLayout)
                    videoView?.getLocationInWindow(videoPlayedLoc)
                    recyclerView.smoothScrollBy(0, videoPlayedLoc[1] - toolbar.height - 80)
                    break
                }
            }
        },
        onVideoWatched10Seconds = { videoID ->
            videoViewModel.getVideoDetail(videoID)
        },
        lifecycle = lifecycle,
        autoPlayOnLoad = true
    )

    private val autoPlayScrollListener = AutoPlayScrollListener { videoPagedAdapter.currentPlayedView }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.channel_video_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager =
            CustomLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = videoPagedAdapter

        recyclerView.addOnScrollListener(autoPlayScrollListener)

        viewModel.initChannelVideo(channelID)
        videoViewModel.initVideoDetailActivity(0)
        observeGetAllVideoResult()
        observeWatchLaterResult()
    }

    override fun onResume() {
        super.onResume()

        videoPagedAdapter.currentPlayedView?.playVideo()
    }

    override fun onPause() {
        super.onPause()

        videoPagedAdapter.currentPlayedView?.pauseVideo()
    }

    fun refresh() {
        viewModel.refreshAllVideo()
    }

    companion object {
        fun newInstance(channelID: Int): Fragment = ChannelVideoFragment(channelID)
    }

    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            videoPagedAdapter.apply { addVideoList(result) }
        })

        viewModel.pagedListNetworkStatusLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_EMPTY_LIST -> showSnackbar("Berhasil")
                        else -> {
                            handleApiError(errorMessage = result.message)
                            { message -> showSnackbar(message) }
                        }
                    }
                }
            }
        })
    }

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast(context, "Berhasil disimpan")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(context, message)
                    }
                }
            }
        })
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }
}
