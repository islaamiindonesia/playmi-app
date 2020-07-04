package id.islaami.playmi.ui.playlist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi.ui.adapter.VideoAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ERROR_CONNECTION
import id.islaami.playmi.util.ERROR_CONNECTION_TIMEOUT
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.playlist_detail_activity.*
import kotlinx.android.synthetic.main.playlist_detail_activity.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailActivity(var playlistId: Int = 0) : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    private var videoAdapter = VideoAdapter { context, menuView, video ->
        PopupMenu(context, menuView).apply {
            inflate(R.menu.menu_popup_playlist_video)

            if (video.channel?.isFollowed != true) {
                menu.getItem(3).title = "Mulai Mengikuti"
            } else {
                menu.getItem(3).title = "Berhenti Mengikuti"
            }

            menu.getItem(2).title = "Hapus dari ${intent.getStringExtra(EXTRA_NAME) ?: ""}"

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popSaveLater -> {
                        viewModel.addWatchLater(video.ID.value())
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
                    else -> false
                }
            }

            show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_detail_activity)

        setupToolbar(toolbar)
        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }

        playlistId = intent.getIntExtra(EXTRA_ID, 0)

        viewModel.initPlaylistDetailActivity(playlistId)
        observeWatchLaterResult()
        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
        observeRemoveFromPlaylistResult()
        observePlaylistChange()
        observePlaylistDelete()

        btnEdit.setOnClickListener {
            editPlaylistDialog(
                playlistId,
                playlistName.text.toString()
            )
        }

        btnDelete.setOnClickListener {
            PlaymiDialogFragment.show(
                supportFragmentManager,
                text = getString(
                    R.string.playlist_remove,
                    intent.getStringExtra(EXTRA_NAME) ?: ""
                ),
                okCallback = { viewModel.deletePlaylist(playlistId) }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML
        menuInflater.inflate(R.menu.menu_playlist_detail, menu)

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

    override fun onResume() {
        super.onResume()

        observeDetail()
        observeAllPlaylist()
    }

    private fun refresh() {
        viewModel.getPlaylistDetail(playlistId)
    }

    private fun showBottomSheet(videoId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.playlist_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialogView.btnNew.setOnClickListener {
            addNewPlaylistDialog(videoId)
            dialog.dismiss()
        }
        dialogView.rvPlaylist.apply {
            adapter = playlistSelectAdapter
            layoutManager = LinearLayoutManager(this@PlaylistDetailActivity)
        }
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnSave.setOnClickListener {
            viewModel.addToPlaylist(videoId, playlistSelectAdapter.getSelectedId())
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun addNewPlaylistDialog(videoId: Int) {
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

    private fun editPlaylistDialog(playlistId: Int, currentName: String) {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.PlaymiMaterialDialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.background = getDrawable(R.drawable.bg_dialog)
        }

        val dialogView = layoutInflater.inflate(R.layout.change_playlist_name_dialog, null)
        dialogView.playlistName.setText(currentName)

        alertDialog.setView(dialogView)
        alertDialog.setPositiveButton("SIMPAN") { dialogInterface, _ ->
            viewModel.changePlaylistName(
                playlistId,
                dialogView.playlistName.text.toString()
            )
            dialogInterface.dismiss()
        }
        alertDialog.setNegativeButton("BATAL") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val dialog = alertDialog.create()
        dialog.show()
    }

    companion object {
        private val EXTRA_ID = "EXTRA_ID"
        private val EXTRA_NAME = "EXTRA_NAME"

        fun startActivity(context: Context?, id: Int, name: String) {
            context?.startActivity(
                Intent(context, PlaylistDetailActivity::class.java)
                    .putExtra(EXTRA_ID, id)
                    .putExtra(EXTRA_NAME, name)
            )
        }
    }

    /* OBSERVERS */
    private fun observeDetail() {
        viewModel.getPlaylistDetailResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    playlistName.text = result.data?.name
                    videoAmount.text = "${result.data?.videoCount} video"

                    val list = result.data?.videos ?: emptyList()

                    recyclerView.adapter = videoAdapter.apply { add(list) }
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    viewModel.allPlaylists()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observeAllPlaylist() {
        viewModel.getPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observeCreatePlaylistResult() {
        viewModel.createPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil membuat daftar putar")
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil disimpan")
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observeRemoveFromPlaylistResult() {
        viewModel.removeFromPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observePlaylistChange() {
        viewModel.changePlaylistNameResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil disimpan")
                    refresh()
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observePlaylistDelete() {
        viewModel.deletePlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil dihapus")
                    finish()
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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
}
