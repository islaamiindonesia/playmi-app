package id.islaami.playmi.ui.playlist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi.ui.adapter.VideoAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.playlistName
import kotlinx.android.synthetic.main.change_playlist_name_dialog.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.btnCancel
import kotlinx.android.synthetic.main.playlist_detail_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class PlaylistDetailActivity : BaseActivity() {
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

    var playlistId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_detail_activity)

        setupToolbar(toolbar)

        playlistId = intent.getIntExtra(EXTRA_ID, 0)

        viewModel.initPlaylistDetailActivity(playlistId)
        observeAllPlaylist()
        observeDetail()

        observeWatchLaterResult()
        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
        observeRemoveFromPlaylistResult()
        observePlaylistChange()
        observePlaylistDelete()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
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
            isIconified = true // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    private fun handleIntent() {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                Log.d("HEIKAMU", "handleIntent: $query")
            }
        }
    }

    private fun refresh() {
        viewModel.getPlaylistDetail(playlistId)
        viewModel.getPlaylist()
    }

    private fun observeDetail() {
        viewModel.getPlaylistDetailResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    playlistName.text = result.data?.name
                    videoAmount.text = "${result.data?.videoCount} video"

                    btnEdit.setOnClickListener {
                        showPlaylistNameDialog(
                            playlistId,
                            result.data?.name.toString()
                        )
                    }

                    btnDelete.setOnClickListener {
                        viewModel.deletePlaylist(playlistId)
                    }

                    val list = result.data?.videos ?: emptyList()

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

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
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

    private fun observeRemoveFromPlaylistResult() {
        viewModel.removeFromPlaylistResultLd.observe(this, Observer { result ->
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

    private fun observeAllPlaylist() {
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

    private fun observePlaylistChange() {
        viewModel.changePlaylistNameResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil disimpan")
                    refresh()

                }
                ERROR -> {
                    showSnackbar(getString(R.string.error_message_default))
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
                    showSnackbar("Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(getString(R.string.error_message_default))
                }
            }
        })
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

    private fun showAddNewPlaylistDialog(videoId: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_new_playlist_dialog, null)

        dialogBuilder.setView(dialogView)
            .setPositiveButton("Simpan") { dialogInterface, i ->
                viewModel.createPlaylist(dialogView.playlistName.text.toString())
                dialogInterface.dismiss()
            }
            .setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun showPlaylistNameDialog(playlistId: Int, currentName: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.change_playlist_name_dialog, null)

        dialogView.playlistName.setText(currentName)

        dialogBuilder.setView(dialogView)

        val dialog = dialogBuilder.create()
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.btnOk.setOnClickListener {
            if (dialogView.playlistName.text.isNotEmpty()) {
                viewModel.changePlaylistName(
                    playlistId,
                    dialogView.playlistName.text.toString()
                )
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    companion object {
        private val EXTRA_ID = "EXTRA_ID"

        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(
                Intent(context, PlaylistDetailActivity::class.java)
                    .putExtra(EXTRA_ID, id)
            )
        }
    }
}
