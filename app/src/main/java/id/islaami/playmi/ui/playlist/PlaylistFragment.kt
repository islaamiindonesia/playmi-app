package id.islaami.playmi.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.PlaylistAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.ui.setting.SettingActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.change_playlist_name_dialog.view.*
import kotlinx.android.synthetic.main.playlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment() {
    private val viewModel: PlaylistViewModel by viewModel()

    private var playlistAdapter = PlaylistAdapter { context, menuView, playlist ->
        PopupMenu(context, menuView).apply {
            inflate(R.menu.menu_popup_playlist)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popChangeName -> {
                        showPlaylistNameDialog(
                            playlist.ID.value(),
                            playlist.name.toString()
                        )
                        true
                    }
                    R.id.popDelete -> {
                        context.showAlertDialogWith2Buttons("Apakah Anda ingin menghapus daftar ${playlist.name}?",
                            positiveText = "Ya",
                            positiveCallback = {
                                viewModel.deletePlaylist(playlist.ID.value())
                                it.dismiss()
                            },
                            negativeText = "Batal",
                            negativeCallback = { it.dismiss() })
                        true
                    }
                    else -> false
                }
            }

            show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener { optionMenuListener(it) }

        viewModel.initPlaylistFragment()
        observeWatchLaterAmount()
        observePlaylistChange()
        observePlaylistDelete()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener {
                refresh()
            }
        }

        btnLater.setOnClickListener {
            WatchLaterActivity.startActivity(context)
        }
    }

    override fun onResume() {
        super.onResume()

        refresh()
    }

    private fun refresh() {
        viewModel.getWatchLaterAmount()
        viewModel.getPlaylist()
    }

    private fun showPlaylistNameDialog(playlistId: Int, currentName: String) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.change_playlist_name_dialog, null)

        dialogView.playlistName.setText(currentName)

        if (dialogBuilder != null) {
            dialogBuilder
                .setView(dialogView)
                .setPositiveButton("Simpan") { dialogInterface, i ->
                    if (dialogView.playlistName.text.isNotEmpty()) {
                        viewModel.changePlaylistName(
                            playlistId,
                            dialogView.playlistName.text.toString()
                        )
                        dialogInterface.dismiss()
                    }
                }
                .setNegativeButton("Batal") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }


            dialogBuilder.create().show()
        }
    }

    private fun observeWatchLaterAmount() {
        viewModel.getWatchLaterAmountLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    watchLaterAmount.text = "${result.data} Video"
                    observePlaylist()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(result.message) {
                        showSnackbar(getString(R.string.error_message_default))
                    }
                }
            }
        })
    }

    private fun observePlaylist() {
        viewModel.getPlaylistResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    val list = result.data ?: emptyList()

                    recyclerView.adapter = playlistAdapter.apply { add(list) }
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(result.message) {
                        context?.showAlertDialog(
                            message = getString(R.string.error_message_default),
                            btnText = "OK"
                        ) { it.dismiss() }
                    }
                }
            }
        })
    }

    private fun observePlaylistChange() {
        viewModel.changePlaylistNameResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil mengubah nama playlist")
                    refresh()

                }
                ERROR -> {
                    showSnackbar(getString(R.string.error_message_default))
                }
            }
        })
    }

    private fun observePlaylistDelete() {
        viewModel.deletePlaylistResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Playlist telah dihapus")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(getString(R.string.error_message_default))
                }
            }
        })
    }

    companion object {
        fun newInstance(): Fragment = PlaylistFragment()
    }
}
