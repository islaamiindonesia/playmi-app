package id.islaami.playmi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.util.ERROR_EMPTY_LIST
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.value
import id.islaami.playmi.util.ui.showAlertDialogWith2Buttons
import id.islaami.playmi.util.ui.showSnackbar
import id.islaami.playmi.util.ui.startRefreshing
import id.islaami.playmi.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.video_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoCategoryFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(context,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_home)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                menu.getItem(0).title = "Simpan ke Tonton Nanti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popWatchLater -> {
                            context.showAlertDialogWith2Buttons(
                                "Simpan ke daftar Tonton Nanti?",
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
                        R.id.popHide -> {
                            context.showAlertDialogWith2Buttons(
                                "Anda tidak akan melihat seluruh video dari kanal ini" +
                                        "\nApakah ingin berhenti mengikuti ${video.channel?.name}?",
                                "Iya",
                                "Batal",
                                positiveCallback = {
                                    viewModel.hideChannel(video.channel?.ID.value())
                                    it.dismiss()
                                },
                                negativeCallback = { it.dismiss() })

                            true
                        }
                        else -> false
                    }
                }

                show()
            }
        })

    var categoryID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }

        arguments?.let { bundle ->
            categoryID = bundle.getInt(EXTRA_CATEGORY, 0)
        }

        viewModel.initVideoCategoryFragment(categoryID)
        observeWatchLaterResult()
        observeFollowResult()
        observeHideResult()
    }

    override fun onResume() {
        super.onResume()

        observeGetAllVideoResult()
    }

    private fun refresh() {
        if (categoryID > 0) {
            viewModel.refreshAllVideoByCategory()
        } else {
            viewModel.refreshAllVideo()
        }
    }

    private fun setupRecyclerView(result: PagedList<Video>?) {
        recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"

        fun newInstance(id: Int) =
            VideoCategoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_CATEGORY, id)
                }
            }
    }

    /* OBSERVERS */
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            setupRecyclerView(result)
        })

        viewModel.pagedListNetworkStatusLd.observe(
            viewLifecycleOwner,
            Observer { result ->
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
                            ERROR_EMPTY_LIST -> showSnackbar("Tidak ada video")
                            else -> {
                                handleApiError(errorMessage = result.message)
                                { message -> showSnackbar(message) }
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
                    showSnackbar("Berhasil disimpan")
                    refresh()
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
                    showSnackbar("Berhasil")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar(getString(R.string.message_channel_hide))
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }
}
