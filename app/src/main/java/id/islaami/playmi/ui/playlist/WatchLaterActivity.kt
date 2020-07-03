package id.islaami.playmi.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi.ui.adapter.VideoAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.watch_later_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WatchLaterActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    private var videoAdapter: VideoAdapter = VideoAdapter { context, menuView, video ->
        PopupMenu(context, menuView).apply {
            inflate(R.menu.menu_popup_later_video)

            if (video.channel?.isFollowed != true) {
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
                        if (video.channel?.isFollowed != true) {
                            viewModel.followChannel(video.channel?.ID.value())
                        } else {
                            viewModel.unfollowChannel(video.channel?.ID.value())
                        }

                        true
                    }
                    else -> false
                }
            }

            show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_later_activity)

        setupToolbar(toolbar)

        viewModel.initWatchLaterActivity()
        observePlaylist()
        observeVideos()

        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
        observeFollowResult()
        observeDeleteResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
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
        viewModel.getWatchLater()
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, WatchLaterActivity::class.java))
        }
    }

    private fun observeVideos() {
        viewModel.watchLaterVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    val list = result.data ?: emptyList()

                    recyclerView.adapter = videoAdapter.apply { add(list) }
                    recyclerView.layoutManager = LinearLayoutManager(this)
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
                }
                SUCCESS -> {
                    showSnackbar("Berhasil membuat daftar putar")
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
                    showSnackbar("Berhasil disimpan")
                }
                ERROR -> {
                    showSnackbar(result.message)
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
                    showSnackbar("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
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
                    showSnackbar("Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }
}
