package id.islaami.playmi2021.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder
import id.islaami.playmi2021.ui.adapter.VideoPagedAdapter
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.ui.video.VideoViewModel
import id.islaami.playmi2021.util.AutoPlayScrollListener
import id.islaami.playmi2021.util.ERROR_EMPTY_LIST
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.*
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.playlist_detail_activity.*
import kotlinx.android.synthetic.main.video_search_activity.*
import kotlinx.android.synthetic.main.video_search_activity.recyclerView
import kotlinx.android.synthetic.main.video_search_activity.swipeRefreshLayout
import kotlinx.android.synthetic.main.video_search_activity.toolbar
import kotlinx.android.synthetic.main.video_update_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoSearchActivity(var searchQuery: String = "") : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_home)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                menu.getItem(0).title = "Simpan ke Tonton Nanti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popWatchLater -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = "Simpan ke daftar Tonton Nanti?",
                                okCallback = { viewModel.watchLater(video.ID.value()) }
                            )

                            true
                        }
                        R.id.popFollow -> {
                            if (video.channel?.isFollowed != true) {
                                PlaymiDialogFragment.show(
                                    fragmentManager = supportFragmentManager,
                                    text = getString(R.string.channel_follow, video.channel?.name),
                                    okCallback = { viewModel.followChannel(video.channel?.ID.value()) }
                                )
                            } else {
                                PlaymiDialogFragment.show(
                                    fragmentManager = supportFragmentManager,
                                    text = getString(
                                        R.string.channel_unfollow,
                                        video.channel?.name
                                    ),
                                    okCallback = { viewModel.unfollowChannel(video.channel?.ID.value()) }
                                )
                            }

                            true
                        }
                        R.id.popHide -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = getString(R.string.channel_hide, video.channel?.name),
                                okCallback = { viewModel.hideChannel(video.channel?.ID.value()) }
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
            Log.i("190401", "onVideoWatched10Seconds videoID: $videoID")
            viewModel.getVideoDetail(videoID)
        },
        lifecycle = lifecycle,
        autoPlayOnLoad = true
    )

    private val autoPlayScrollListener = AutoPlayScrollListener { videoPagedAdapter.currentPlayedView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_search_activity)

        setupToolbar(toolbar)

        viewModel.initVideoDetailActivity(0)
        viewModel.initVideoSearchActivity()
        observeWatchLaterResult()
        observeFollowResult()
        observeUnfollowResult()
        observeHideResult()

        recyclerView.layoutManager =
            CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = videoPagedAdapter

        recyclerView.addOnScrollListener(autoPlayScrollListener)

        handleIntent()

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
            queryHint = "Cari Video"
            setQuery(this@VideoSearchActivity.searchQuery, false)
        }

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    override fun onResume() {
        super.onResume()

        observeGetAllVideoResult()

        videoPagedAdapter.currentPlayedView?.playVideo()
    }

    override fun onPause() {
        super.onPause()

        videoPagedAdapter.currentPlayedView?.pauseVideo()
    }

    private fun handleIntent() {
        swipeRefreshLayout.startRefreshing()

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                this.searchQuery = query
                viewModel.getAllVideo(query)
            }
        }
    }

    private fun refresh() {
        viewModel.refreshAllVideo()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, VideoSearchActivity::class.java))
        }
    }

    /* OBSERVER */
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            videoPagedAdapter.apply { addVideoList(result) }
        })

        viewModel.networkStatusLd.observe(
            this,
            Observer { result ->
                when (result?.status) {
                    LOADING -> {
                    }
                    SUCCESS -> {
                        swipeRefreshLayout.stopRefreshing()
                    }
                    ERROR -> {
                        swipeRefreshLayout.stopRefreshing()
                        when (result.message) {
                            ERROR_EMPTY_LIST -> showSnackbar("Tidak ada video")
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
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil disimpan")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeUnfollowResult() {
        viewModel.unfollowResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast(getString(R.string.message_channel_hide))
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }
}
