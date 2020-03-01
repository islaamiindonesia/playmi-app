package com.example.playmi.ui.channel_following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.ChannelFollowingAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.handleApiError
import com.example.playmi.util.value
import id.co.badr.commerce.mykopin.util.ui.showAlertDialog
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import id.co.badr.commerce.mykopin.util.ui.startRefreshing
import id.co.badr.commerce.mykopin.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowingFragment : BaseFragment() {
    private val viewModel: ChannelFollowingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.following_hidden_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.initFollowingFragment()
        observeGetChannelFollow()

        observeUnfollow()
        observeHide()
    }

    private fun refresh() {
        viewModel.getChannelFollow()
    }

    private fun observeGetChannelFollow() {
        viewModel.getChannelFollowResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    if (!result.data.isNullOrEmpty()) {
                        recyclerView.adapter = ChannelFollowingAdapter(result.data,
                            popMenu = { context, view, channel ->
                                PopupMenu(context, view).apply {
                                    inflate(R.menu.menu_popup_channel_follow)

                                    setOnMenuItemClickListener { item ->
                                        when (item.itemId) {
                                            R.id.popUnfollow -> {
                                                viewModel.unfollowChannel(channel.ID.value())

                                                true
                                            }
                                            R.id.popHide -> {
                                                viewModel.hideChannel(channel.ID.value())

                                                true
                                            }
                                            else -> false
                                        }
                                    }

                                    show()
                                }
                            })

                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(errorMessage = result.message) { message ->
                        showSnackbar(message)
                    }
                }
            }
        })
    }

    private fun observeUnfollow() {
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
                    handleApiError(errorMessage = result.message) { message ->
                        context?.showAlertDialog(
                            message = message,
                            btnText = "OK",
                            btnCallback = { it.dismiss() }
                        )
                    }
                }
            }
        })
    }

    private fun observeHide() {
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
                    handleApiError(errorMessage = result.message) { message ->
                        context?.showAlertDialog(
                            message = message,
                            btnText = "OK",
                            btnCallback = { it.dismiss() }
                        )
                    }
                }
            }
        })
    }

    companion object {
        fun newInstance() = FollowingFragment()
    }
}
