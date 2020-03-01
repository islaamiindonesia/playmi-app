package com.example.playmi.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.PlaylistAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.*
import kotlinx.android.synthetic.main.change_playlist_name_dialog.view.*
import kotlinx.android.synthetic.main.playlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.initPlaylistFragment()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        watchLaterLayout.setOnClickListener {
            WatchLaterActivity.startActivity(context)
        }

        observeWatchLaterAmount()

        observePlaylistChange()
        observePlaylistDelete()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
            }
        }
    }

    private fun refresh() {
        viewModel.getWatchLaterAmount()
        viewModel.getPlaylists()
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

                    playlistAdapter = PlaylistAdapter(result.data,
                        popMenu = { context, menuView, playlist ->
                            PopupMenu(context, menuView).apply {
                                inflate(R.menu.menu_popup_playlist)

                                setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.popChangeName -> {
                                            showPlaylistNameDialog(playlist.ID.value(), playlist.name.toString())
                                            true
                                        }
                                        R.id.popDelete -> {
                                            context.showAlertDialogWith2Buttons("Apakah Anda ingin menghapus ini?",
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
                        })

                    recyclerView.adapter = playlistAdapter
                    recyclerView.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                }
            }
        })
    }

    private fun observePlaylistChange() {
        viewModel.changePlaylistNameResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    context?.showShortToast("Berhasil disimpan")
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    context?.showShortToast("Gagal disimpan. Silahkan coba sesaat lagi.")
                }
            }
        })
    }

    private fun observePlaylistDelete() {
        viewModel.deletePlaylistResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    context?.showShortToast("Berhasil dihapus")
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    context?.showShortToast("Gagal dihapus. Silahkan coba sesaat lagi.")
                }
            }
        })
    }

    companion object {
        fun newInstance(): Fragment = PlaylistFragment()
    }
}
