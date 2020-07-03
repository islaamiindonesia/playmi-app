package id.islaami.playmi.ui.channel_following

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.ChannelFollowingAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelFollowingFragment : BaseFragment() {
    private val viewModel: OrganizeChannelViewModel by viewModel()

    private var adapter = ChannelFollowingAdapter { context, view, channel ->
        PopupMenu(context, view, Gravity.END).apply {
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

        search.apply {
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.getChannelFollow(newText)

                    return true
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        refresh()
    }

    private fun refresh() {
        viewModel.getChannelFollow()
    }

    companion object {
        fun newInstance() = ChannelFollowingFragment()
    }

    private fun observeChannel() {
        viewModel.channelFollowingLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> { }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()

                    val list = result.data ?: emptyList()

                    recyclerView.adapter = adapter.apply {
                        add(list)
                        notifyDataSetChanged()
                    }
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(errorMessage = result.message) { message ->
                        context?.showShortToast(message)
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
                    context?.showShortToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        context?.showShortToast(message)
                    }
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
                    context?.showShortToast(getString(R.string.message_channel_hide))
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        context?.showShortToast(message)
                    }
                }
            }
        })
    }
}
