package id.islaami.playmi.ui.channel_following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.ChannelFollowingAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.value
import id.islaami.playmi.util.ui.showSnackbar
import id.islaami.playmi.util.ui.startRefreshing
import id.islaami.playmi.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelFollowingFragment : BaseFragment() {
    private val viewModel: OrganizeChannelViewModel by viewModel()

    private var adapter = ChannelFollowingAdapter { context, view, channel ->
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.following_hidden_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initFollowingFragment()
        observeChannel()
        observeUnfollow()
        observeHide()

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
        viewModel.getChannelFollow()
    }

    private fun observeChannel() {
        viewModel.getChannelFollowResultLd.observe(this, Observer { result ->
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

    private fun observeUnfollow() {
        viewModel.followingResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeHide() {
        viewModel.channelStatusResultLd.observe(viewLifecycleOwner, Observer { result ->
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

    companion object {
        fun newInstance() = ChannelFollowingFragment()
    }
}
