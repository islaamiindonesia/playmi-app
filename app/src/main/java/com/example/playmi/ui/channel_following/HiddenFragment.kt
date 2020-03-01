package com.example.playmi.ui.channel_following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.ChannelHiddenAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus
import com.example.playmi.util.handleApiError
import id.co.badr.commerce.mykopin.util.ui.showAlertDialog
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import id.co.badr.commerce.mykopin.util.ui.startRefreshing
import id.co.badr.commerce.mykopin.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HiddenFragment : BaseFragment() {
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

        viewModel.initHiddenFragment()
        observeGetChannelHide()

        observeShow()
    }

    private fun refresh() {
        viewModel.getChannelHidden()
    }

    private fun observeGetChannelHide() {
        viewModel.getChannelHiddenResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                ResourceStatus.LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                ResourceStatus.SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    if (!result.data.isNullOrEmpty()) {
                        recyclerView.adapter = ChannelHiddenAdapter(result.data)
                        { channelId -> viewModel.showChannel(channelId) }

                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }
                }
                ResourceStatus.ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(errorMessage = result.message) { message ->
                        showSnackbar(message)
                    }
                }
            }
        })
    }

    private fun observeShow() {
        viewModel.hideChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                ResourceStatus.LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                ResourceStatus.SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    refresh()
                }
                ResourceStatus.ERROR -> {
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
        fun newInstance() = HiddenFragment()
    }
}
