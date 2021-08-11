package id.islaami.playmi2021.ui.playlist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.widget.RelativeLayout
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.PlaybackViewHolder
import id.islaami.playmi2021.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi2021.ui.adapter.VideoAdapter
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.ui.video.VideoViewModel
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.playlist_detail_activity.*
import kotlinx.android.synthetic.main.video_update_fragment.*
import kotlinx.android.synthetic.main.watch_later_activity.*
import kotlinx.android.synthetic.main.watch_later_activity.recyclerView
import kotlinx.android.synthetic.main.watch_later_activity.swipeRefreshLayout
import kotlinx.android.synthetic.main.watch_later_activity.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class WatchLaterActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()
    private val videoViewModel: VideoViewModel by viewModel()

    private lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    private var videoAdapter: VideoAdapter = VideoAdapter(
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_later_video)

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popSavePlaylist -> {
                            showBottomSheet(video.ID.value())
                            true
                        }
                        R.id.popDeleteList -> {
                            viewModel.deleteFromLater(video.ID.value())
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
            videoViewModel.getVideoDetail(videoID)
        },
        lifecycle = lifecycle,
        autoPlayOnLoad = true
    )

    private val autoPlayScrollListener = AutoPlayScrollListener { videoAdapter.currentPlayedView }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_later_activity)

        setupToolbar(toolbar)

        recyclerView.layoutManager =
            CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = videoAdapter

        recyclerView.addOnScrollListener(autoPlayScrollListener)

        videoViewModel.initVideoDetailActivity(0)
        viewModel.initWatchLaterActivity()
        observeWatchLater()

        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
        observeFollowResult()
        observeUnfollowResult()
        observeDeleteResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }
    }

    override fun onResume() {
        super.onResume()

        videoAdapter.currentPlayedView?.playVideo()
    }

    override fun onPause() {
        super.onPause()

        videoAdapter.currentPlayedView?.pauseVideo()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML
        menuInflater.inflate(R.menu.menu_watch_later, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.mainSearch)?.actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = "Cari Video"
            isIconified = true // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun refresh() {
        viewModel.getWatchLater()
        viewModel.allPlaylists()
    }

    private fun showBottomSheet(videoId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.playlist_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialogView.btnNew.setOnClickListener {
            showAddNewPlaylistDialog(videoId)
            dialog.dismiss()
        }
        dialogView.rvPlaylist.apply {
            adapter = playlistSelectAdapter
            layoutManager = LinearLayoutManager(this@WatchLaterActivity)
        }
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnSave.setOnClickListener {
            viewModel.addToManyPlaylists(videoId, playlistSelectAdapter.selectedIds)
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showAddNewPlaylistDialog(videoId: Int) {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.PlaymiMaterialDialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.background = getDrawable(R.drawable.bg_dialog)
        }

        val dialogView = layoutInflater.inflate(R.layout.add_new_playlist_dialog, null)
        alertDialog.setView(dialogView)
        alertDialog.setPositiveButton("SIMPAN") { dialogInterface, _ ->
            viewModel.createPlaylist(dialogView.playlistName.text.toString(), videoId)
            dialogInterface.dismiss()
        }
        alertDialog.setNegativeButton("BATAL") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val dialog = alertDialog.create()
        dialog.show()

        val positiveBtn = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        positiveBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))

        val negativeBtn = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, WatchLaterActivity::class.java))
        }
    }

    private fun observeWatchLater() {
        viewModel.watchLaterVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    val list = result.data ?: emptyList()

                    videoAdapter.apply { add(list) }

                    observePlaylist()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) {
                                showLongToast(it)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun observePlaylist() {
        viewModel.getPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    recyclerView.setVisibilityToVisible()

                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }

                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) {
                                showLongToast(it)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun observeCreatePlaylistResult() {
        viewModel.createPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil membuat daftar putar")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) {
                        showLongToast(it)
                    }
                }
            }
        })
    }

    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil disimpan")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) {
                        showLongToast(it)
                    }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) {
                        showLongToast(it)
                    }
                }
            }
        })
    }

    private fun observeUnfollowResult() {
        viewModel.unfollowChannelResultLd.observe(this, Observer { result ->
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

    private fun observeDeleteResult() {
        viewModel.deleteFromLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) {
                        showLongToast(it)
                    }
                }
            }
        })
    }
}
