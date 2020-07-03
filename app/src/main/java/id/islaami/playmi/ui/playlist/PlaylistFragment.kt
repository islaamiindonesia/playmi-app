package id.islaami.playmi.ui.playlist

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
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
        PopupMenu(context, menuView, Gravity.END).apply {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.menu_main)

        // Get the SearchView and set the searchable configuration
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (toolbar.menu.findItem(R.id.mainSearch).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = "Cari Video"
            isIconified = true // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mainSetting -> {
                    SettingActivity.startActivity(context)

                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }

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

        dialogBuilder?.setView(dialogView)

        val dialog = dialogBuilder?.create()
        dialogView.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        dialogView.btnOk.setOnClickListener {
            if (dialogView.playlistName.text.isNotEmpty()) {
                viewModel.changePlaylistName(
                    playlistId,
                    dialogView.playlistName.text.toString()
                )
                dialog?.dismiss()
            }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
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
                        context?.showShortToast(getString(R.string.error_message_default))
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
                    context?.showShortToast("Berhasil mengubah nama playlist")
                    refresh()

                }
                ERROR -> {
                    context?.showShortToast(getString(R.string.error_message_default))
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
                    context?.showShortToast("Playlist telah dihapus")
                    refresh()
                }
                ERROR -> {
                    context?.showShortToast(getString(R.string.error_message_default))
                }
            }
        })
    }

    companion object {
        fun newInstance(): Fragment = PlaylistFragment()
    }
}
