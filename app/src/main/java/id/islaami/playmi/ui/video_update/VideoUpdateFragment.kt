package id.islaami.playmi.ui.video_update

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.ui.setting.SettingActivity
import id.islaami.playmi.util.ERROR_EMPTY_LIST
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.video_update_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoUpdateFragment : BaseFragment() {
    private val viewModel: VideoUpdateViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(context,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView, Gravity.END).apply {
                inflate(R.menu.menu_popup_video_update)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popWatchLater -> {
                            context.showAlertDialogWith2Buttons(
                                "Simpan ke daftar ic_watch Nanti?",
                                "Iya",
                                "Batal",
                                positiveCallback = {
                                    viewModel.watchLater(video.ID.value())
                                    it.dismiss()
                                },
                                negativeCallback = { it.dismiss() })

                            true
                        }
                        R.id.popFollow -> {
                            if (video.channel?.isFollowed != true) {
                                context.showAlertDialogWith2Buttons(
                                    "Apakah ingin mulai mengikuti ${video.channel?.name}?",
                                    "Iya",
                                    "Batal",
                                    positiveCallback = {
                                        viewModel.followChannel(video.channel?.ID.value())
                                        it.dismiss()
                                    },
                                    negativeCallback = { it.dismiss() })
                            } else {
                                context.showAlertDialogWith2Buttons(
                                    "Apakah ingin berhenti mengikuti ${video.channel?.name}?",
                                    "Iya",
                                    "Batal",
                                    positiveCallback = {
                                        viewModel.unfollowChannel(video.channel?.ID.value())
                                        it.dismiss()
                                    },
                                    negativeCallback = { it.dismiss() })
                            }

                            true
                        }
                        else -> false
                    }
                }

                show()
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_update_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener {
                refresh()
            }
        }

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
    }

    override fun onResume() {
        super.onResume()

        viewModel.initVideoUpdateFragment()
        observeGetAllVideoResult()
        observeFollowResult()
        observeWatchLaterResult()
    }

    private fun refresh() {
        viewModel.refreshAllVideoUpdate()
    }

    private fun setupRecyclerView(result: PagedList<Video>?) {
        recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        fun newInstance(): Fragment = VideoUpdateFragment()
    }

    private fun observeGetAllVideoResult() {
        viewModel.videoUpdatePagedListResultLd.observe(this, Observer { result ->
            setupRecyclerView(result)
        })

        viewModel.networkStatusLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_EMPTY_LIST -> showSnackbar("Anda belum mengikuti kanal manapun")
                        else -> {
                            handleApiError(errorMessage = result.message)
                            { message -> showSnackbar(message) }
                        }
                    }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
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

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    context?.showShortToast("Berhasil disimpan")
                    refresh()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        "DATA_EXIST" -> showSnackbar("Anda telah menyimpan video ke ic_watch Nanti")
                        else -> {
                            handleApiError(errorMessage = result.message)
                            { showSnackbar(it) }
                        }
                    }
                }
            }
        })
    }
}
