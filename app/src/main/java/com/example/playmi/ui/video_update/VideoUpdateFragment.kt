package com.example.playmi.ui.video_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.adapter.VideoPagedAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.*
import kotlinx.android.synthetic.main.video_category_fragment.*
import kotlinx.android.synthetic.main.video_update_fragment.*
import kotlinx.android.synthetic.main.video_update_fragment.recyclerView
import kotlinx.android.synthetic.main.video_update_fragment.swipeRefreshLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoUpdateFragment : BaseFragment() {
    private val viewModel: VideoUpdateViewModel by viewModel()

    lateinit var videoPagedAdapter: VideoPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_update_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoPagedAdapter = VideoPagedAdapter(context,
            popMenu = { context, menuView, video ->
                PopupMenu(context, menuView).apply {
                    inflate(R.menu.menu_popup_video_update)

                    if (video.followStatus != true) {
                        menu.getItem(1).title = "Mulai Mengikuti"
                    } else {
                        menu.getItem(1).title = "Berhenti Mengikuti"
                    }

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

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
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

    private fun observeGetAllVideoResult() {
        // observe login result
        viewModel.getVideoUpdatePagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            setupRecyclerView(result)
        })

        viewModel.getVideoUpdateNetworkStatusLd.observe(viewLifecycleOwner, Observer { result ->
                when (result?.status) {
                    LOADING -> {
                    }
                    SUCCESS -> {
                    }
                    ERROR -> {
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
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun setupRecyclerView(result: PagedList<Video>?) {
        recyclerView.adapter = videoPagedAdapter.apply {
            addVideoList(result)
            this.notifyDataSetChanged()
        }
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        fun newInstance(): Fragment = VideoUpdateFragment()
    }
}
