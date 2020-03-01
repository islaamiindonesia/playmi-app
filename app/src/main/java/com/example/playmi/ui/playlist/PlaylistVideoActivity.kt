package com.example.playmi.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.PlaylistSelectAdapter
import com.example.playmi.ui.adapter.VideoAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.value
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.co.badr.commerce.mykopin.util.ui.showShortToast
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_video_activity.*
import kotlinx.android.synthetic.main.video_detail_bottom_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistVideoActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var playlistSelectAdapter: PlaylistSelectAdapter
    lateinit var videoAdapter: VideoAdapter

    var playlistId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_video_activity)

        playlistId = intent.getIntExtra(EXTRA_ID, 0)

        viewModel.initPlaylistDetailActivity(playlistId)
        observePlaylist()
        observeVideos()

        observeRemoveFromPlaylistResult()
    }

    private fun refresh() {
        viewModel.getPlaylistVideo(playlistId)
        viewModel.getPlaylists()
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
            layoutManager = LinearLayoutManager(this@PlaylistVideoActivity)
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

    private fun observeVideos() {
        viewModel.getPlaylistVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    videoAdapter = VideoAdapter(result.data,
                        popMenu = { context, menuView, video ->
                            PopupMenu(context, menuView).apply {
                                inflate(R.menu.menu_popup_playlist_video)

                                if (video.followStatus != true) {
                                    menu.getItem(3).title = "Mulai Mengikuti"
                                } else {
                                    menu.getItem(3).title = "Berhenti Mengikuti"
                                }

                                setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.popSaveLater -> {
                                            viewModel.watchLater(video.ID.value())
                                            true
                                        }
                                        R.id.popSavePlaylist -> {
                                            showBottomSheet(video.ID.value())
                                            true
                                        }
                                        R.id.popDeleteList -> {
                                            viewModel.removeFromPlaylist(
                                                video.ID.value(),
                                                playlistId
                                            )
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

                    recyclerView.adapter = videoAdapter
                    recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })

        observeWatchLaterResult()
        observeAddToPlaylistResult()
    }

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showShortToast("Berhasil disimpan")
                }
                ERROR -> {
                    showSnackbar(result.message)
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
                    showShortToast("Berhasil disimpan")
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeRemoveFromPlaylistResult() {
        viewModel.removeFromPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showShortToast("Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
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
                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {

                }
            }
        })
    }

    companion object {
        private val EXTRA_ID = "EXTRA_ID"

        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(
                Intent(context, PlaylistVideoActivity::class.java)
                    .putExtra(EXTRA_ID, id)
            )
        }
    }
}
