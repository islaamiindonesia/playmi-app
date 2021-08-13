package id.islaami.playmi2021.ui.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder
import id.islaami.playmi2021.ui.adapter.VideoPagedAdapter
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.playlist_detail_activity.*
import kotlinx.android.synthetic.main.video_subcategory_activity.*
import kotlinx.android.synthetic.main.video_subcategory_activity.recyclerView
import kotlinx.android.synthetic.main.video_subcategory_activity.swipeRefreshLayout
import kotlinx.android.synthetic.main.video_subcategory_activity.toolbar
import kotlinx.android.synthetic.main.video_update_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoSubcategoryActivity(
    var categoryID: Int = 0,
    var subcategoryID: Int = 0
) : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView, Gravity.END).apply {
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
                    recyclerView.customSmoothScrollToPosition(nextPosition)
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
        setContentView(R.layout.video_subcategory_activity)

        setupToolbar(toolbar, title = intent.getStringExtra(SUBCATEGORY_NAME))

        categoryID = intent.getIntExtra(CATEGORY_ID, 0)
        subcategoryID = intent.getIntExtra(SUBCATEGORY_ID, 0)

        viewModel.initVideoSubcategoryActivity(categoryID, subcategoryID)
        viewModel.initVideoDetailActivity(0)

        recyclerView.layoutManager =
            CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = videoPagedAdapter

        recyclerView.addOnScrollListener(autoPlayScrollListener)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.getAllVideoBySubcategory(categoryID, subcategoryID)
    }

    override fun onResume() {
        super.onResume()

        swipeRefreshLayout.startRefreshing()

        observeGetAllVideoResult()
        observeWatchLaterResult()
        observeFollowResult()
        observeUnfollowResult()
        observeHideResult()

        val position =
            PreferenceManager.getDefaultSharedPreferences(this).getInt("SUBCATEGORY_SCROLL", 0)
        recyclerView.scrollToPosition(position)

        videoPagedAdapter.currentPlayedView?.playVideo()
    }

    override fun onPause() {
        super.onPause()

        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null) {
            val position =
                (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("SUBCATEGORY_SCROLL", position)
                .apply()
        }

        videoPagedAdapter.currentPlayedView?.pauseVideo()
    }

    private fun refresh() {
        viewModel.refreshAllVideoBySub()
    }

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SUBCATEGORY_ID = "SUBCATEGORY_ID"
        const val SUBCATEGORY_NAME = "SUBCATEGORY_NAME"

        fun startActivity(
            context: Context?,
            categoryId: Int,
            subcategoryId: Int,
            subcategoryName: String
        ) {
            context?.startActivity(Intent(context, VideoSubcategoryActivity::class.java).apply {
                putExtra(CATEGORY_ID, categoryId)
                putExtra(SUBCATEGORY_ID, subcategoryId)
                putExtra(SUBCATEGORY_NAME, subcategoryName)
            })
        }
    }

    /* OBSERVERS */
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            videoPagedAdapter.apply { addVideoList(result) }
        })

        viewModel.networkStatusLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {}
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    recyclerView.setVisibilityToVisible()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_EMPTY_LIST -> {
                            recyclerView.setVisibilityToGone()
                        }
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) { showLongToast(it) }
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
            when (result.status) {
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
            when (result.status) {
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
            when (result.status) {
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
