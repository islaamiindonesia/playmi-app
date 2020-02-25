package com.example.playmi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.custom_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModel()

    lateinit var videoPagedAdapter: VideoPagedAdapter

    var categoryName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
            }
        }

        videoPagedAdapter = VideoPagedAdapter(context,
            popMenuItemOnClickListener = { item, video ->
                when (item.itemId) {
                    R.id.popWatchLater -> {
                        context?.showAlertDialogWith2Buttons(
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
                    R.id.popHideChannel -> {
                        viewModel.hideChannel(video.channelID.value())

                        true
                    }
                    else -> false
                }
            })

        arguments?.let { bundle ->
            categoryName = bundle.getString(EXTRA_CATEGORY_NAME)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.initCustomFragment(categoryName)
        observeGetAllVideoResult()

        observeHideResult()
        observeFollowResult()
        observeWatchLaterResult()
    }

    private fun refresh() {
        viewModel.refreshAllVideo()
        observeGetAllVideoResult()
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

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
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

    private fun observeGetAllVideoResult() {
        // observe login result
        viewModel.getVideoPagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            setupRecyclerView(result)
        })

        viewModel.getVideoPagedListNetworkStatusLd.observe(
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
        private const val EXTRA_CATEGORY_NAME = "EXTRA_CATEGORY_NAME"

        fun newInstance(name: String) =
            CustomFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_CATEGORY_NAME, name)
                }
            }
    }
}
