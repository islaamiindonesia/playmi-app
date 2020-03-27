package id.islaami.playmi.ui.channel_following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.ChannelHiddenAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.showSnackbar
import id.islaami.playmi.util.ui.startRefreshing
import id.islaami.playmi.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelHiddenFragment : BaseFragment() {
    private val viewModel: OrganizeChannelViewModel by viewModel()

    private var adapter = ChannelHiddenAdapter { channelId -> viewModel.showChannel(channelId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.following_hidden_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initHiddenFragment()
        observeChannel()
        observeShow()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
    }

    override fun onResume() {
        super.onResume()

        refresh()
    }

    private fun refresh() {
        viewModel.getChannelHidden()
    }

    private fun observeChannel() {
        viewModel.getChannelHiddenResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()

                    val list = result.data ?: emptyList()

                    recyclerView.adapter = adapter.apply { add(list) }
                    recyclerView.layoutManager = LinearLayoutManager(context)
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

    private fun observeShow() {
        viewModel.channelStatusResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar(getString(R.string.message_channel_show))
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    companion object {
        fun newInstance() = ChannelHiddenFragment()
    }
}
