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
import id.islaami.playmi.util.*
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.following_hidden_fragment.*
import kotlinx.android.synthetic.main.following_hidden_fragment.recyclerView
import kotlinx.android.synthetic.main.following_hidden_fragment.swipeRefreshLayout
import kotlinx.android.synthetic.main.video_category_fragment.*
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
        observeUnfollow()

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

        observeChannel()
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
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
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

                    when (result.message) {
                        ERROR_EMPTY_LIST -> {
                            emptyText.setVisibilityToVisible()
                            recyclerView.setVisibilityToGone()
                        }
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                context,
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                context,
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) { message ->
                                showLongToast(context, message)
                            }
                        }
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
                    showLongToast(context, "Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(context, message)
                    }
                }
            }
        })
    }
}
