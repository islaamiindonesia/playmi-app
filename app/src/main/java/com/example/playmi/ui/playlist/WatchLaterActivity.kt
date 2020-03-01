package com.example.playmi.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.adapter.PlaylistSelectAdapter
import com.example.playmi.ui.adapter.VideoAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.value
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.co.badr.commerce.mykopin.util.ui.*
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_fragment.*
import kotlinx.android.synthetic.main.video_detail_bottom_sheet.view.*
import kotlinx.android.synthetic.main.watch_later_activity.*
import kotlinx.android.synthetic.main.watch_later_activity.recyclerView
import kotlinx.android.synthetic.main.watch_later_activity.successLayout
import kotlinx.android.synthetic.main.watch_later_activity.swipeRefreshLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class WatchLaterActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    lateinit var videos: List<Video>
    lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_later_activity)

        viewModel.initWatchLaterActivity()
        observePlaylist()
        observeVideos()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
            }
        }
    }

    private fun showBottomSheet(videoId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.video_detail_bottom_sheet, null)
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
            viewModel.addToPlaylist(videoId, playlistSelectAdapter.getSelectedId())
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showAddNewPlaylistDialog(videoId: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_new_playlist_dialog, null)

        dialogBuilder.setView(dialogView)
            .setPositiveButton("Simpan") { dialogInterface, i ->
                viewModel.createPlaylist(dialogView.playlistName.text.toString(), videoId)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun refresh() {
        viewModel.getLaterVideos()
    }

    private fun observeVideos() {
        viewModel.getLaterVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()
                    videos = result.data ?: emptyList()

                    if (videos.isEmpty()) {
                        showAlertDialog(
                            "Daftar putar kosong.",
                            "OK"
                        ) {
                            it.dismiss()
                            onBackPressed()
                        }
                    } else {
                        videoAdapter = VideoAdapter(videos,
                            popMenu = { context, menuView, video ->
                                PopupMenu(context, menuView).apply {
                                    inflate(R.menu.menu_popup_later_video)

                                    if (video.followStatus != true) {
                                        menu.getItem(2).title = "Mulai Mengikuti"
                                    } else {
                                        menu.getItem(2).title = "Berhenti Mengikuti"
                                    }

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
                                            R.id.popFollow -> {
                                                if (video.followStatus != true) {
                                                    viewModel.followChannel(video.channelID.value())
                                                } else {
                                                    viewModel.unfollowChannel(video.channelID.value())
                                                }

                                                true
                                            }
                                            else -> false
                                        }
                                    }

                                    show()
                                }
                            })

                        setupRecyclerView()
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showAlertDialog(
                        message = "Terjadi kesalahan, silahkan coba sesaat lagi.",
                        btnText = "OK",
                        btnCallback = {
                            it.dismiss()
                            onBackPressed()
                        }
                    )
                }
            }
        })

        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
        observeFollowResult()
        observeDeleteResult()
    }

    private fun observePlaylist() {
        viewModel.getPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {

                }
            }
        })
    }

    private fun observeCreatePlaylistResult() {
        viewModel.createPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    showShortToast("Berhasil disimpan")
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    showShortToast("Berhasil disimpan")
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    refresh()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeDeleteResult() {
        viewModel.deleteFromLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    refresh()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = videoAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, WatchLaterActivity::class.java))
        }
    }
}
