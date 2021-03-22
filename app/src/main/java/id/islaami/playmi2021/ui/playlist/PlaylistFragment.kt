package id.islaami.playmi2021.ui.playlist

import android.app.SearchManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.PlaylistAdapter
import id.islaami.playmi2021.ui.base.BaseFragment
import id.islaami.playmi2021.ui.base.BaseRecyclerViewFragment
import id.islaami.playmi2021.ui.setting.SettingActivity
import id.islaami.playmi2021.util.ERROR_CONNECTION
import id.islaami.playmi2021.util.ERROR_CONNECTION_TIMEOUT
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.*
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.change_playlist_name_dialog.view.*
import kotlinx.android.synthetic.main.playlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment(), BaseRecyclerViewFragment {
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
                        PlaymiDialogFragment.show(
                            childFragmentManager,
                            text = getString(R.string.playlist_remove, playlist.name),
                            okCallback = { viewModel.deletePlaylist(playlist.ID.value()) }
                        )
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
    ): View? = inflater.inflate(R.layout.playlist_fragment, container, false)

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
        observePlaylist()
        observeWatchLaterAmount()

        observePlaylistChange()
        observePlaylistDelete()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }

        btnLater.setOnClickListener {
            WatchLaterActivity.startActivity(context)
        }

        swipeRefreshLayout.startRefreshing()
    }

    override fun onResume() {
        super.onResume()

        refresh()
    }

    private fun refresh() {
        viewModel.allPlaylists()
    }

    private fun showPlaylistNameDialog(playlistId: Int, currentName: String) {
        context?.let { MaterialAlertDialogBuilder(it, R.style.PlaymiMaterialDialog) }?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background = getDrawable(context, R.drawable.bg_dialog)
            }

            val dialogView = layoutInflater.inflate(R.layout.change_playlist_name_dialog, null)
            dialogView.playlistName.setText(currentName)

            setView(dialogView)
            setPositiveButton("SIMPAN") { dialogInterface, _ ->
                viewModel.changePlaylistName(
                    playlistId,
                    dialogView.playlistName.text.toString()
                )
                dialogInterface.dismiss()
            }
            setNegativeButton("BATAL") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            val dialog = create()
            dialog.show()
        }
    }

    companion object {
        fun newInstance(): Fragment = PlaylistFragment()
    }

    private fun observeWatchLaterAmount() {
        viewModel.getWatchLaterAmountLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    watchLaterAmount.text = "${result.data} Video"
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) {
                                showLongToast(context, it)
                            }
                        }
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
                    val list = result.data ?: emptyList()

                    if (list.isEmpty()) {
                        playlist.setVisibilityToGone()
                    } else {
                        playlist.setVisibilityToVisible()
                        recyclerView.adapter = playlistAdapter.apply { add(list) }
                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }

                    viewModel.getWatchLaterAmount()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) {
                                showLongToast(context, it)
                            }
                        }
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
                    showLongToast(context, "Berhasil mengubah nama playlist")
                    refresh()

                }
                ERROR -> {
                    showLongToast(context, getString(R.string.error_message_default))
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
                    showLongToast(context, "Berhasil dihapus")
                    refresh()
                }
                ERROR -> {
                    showLongToast(context, getString(R.string.error_message_default))
                }
            }
        })
    }

    override fun scrollToTop() {
        successLayout.smoothScrollTo(0, 0)
    }
}
