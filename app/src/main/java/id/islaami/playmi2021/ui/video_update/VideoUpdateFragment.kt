package id.islaami.playmi2021.ui.video_update

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.MainActivity
import id.islaami.playmi2021.ui.adapter.VideoPagedAdapter
import id.islaami.playmi2021.ui.base.BaseFragment
import id.islaami.playmi2021.ui.base.BaseHasFloatingButtonFragment
import id.islaami.playmi2021.ui.base.BaseRecyclerViewFragment
import id.islaami.playmi2021.ui.setting.SettingActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.video_update_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoUpdateFragment() : BaseFragment(), BaseRecyclerViewFragment,
    BaseHasFloatingButtonFragment {
    private val viewModel: VideoUpdateViewModel by viewModel()
    private var shuffle: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    private var videoPagedAdapter = VideoPagedAdapter(context,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView, Gravity.END).apply {
                inflate(R.menu.menu_popup_video_update)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popWatchLater -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = childFragmentManager,
                                text = "Simpan ke daftar Tonton Nanti?",
                                okCallback = { viewModel.watchLater(video.ID.value()) }
                            )

                            true
                        }
                        R.id.popFollow -> {
                            if (video.channel?.isFollowed != true) {
                                PlaymiDialogFragment.show(
                                    fragmentManager = childFragmentManager,
                                    text = getString(R.string.channel_follow, video.channel?.name),
                                    okCallback = { viewModel.followChannel(video.channel?.ID.value()) }
                                )
                            } else {
                                PlaymiDialogFragment.show(
                                    fragmentManager = childFragmentManager,
                                    text = getString(
                                        R.string.channel_unfollow,
                                        video.channel?.name
                                    ),
                                    okCallback = { viewModel.unfollowChannel(video.channel?.ID.value()) }
                                )
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
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }

        (activity as? MainActivity)?.floatingActionButton?.let { fab ->
            fab.setOnClickListener {
                if (shuffle == 0) {
                    swipeRefreshLayout.isRefreshing = true
                    viewModel.changeParam(++shuffle)
                    fab.setImageResource(R.drawable.ic_sort_black)
                    showLongToast(requireContext(), "Diurutkan secara acak")
                } else {
                    swipeRefreshLayout.isRefreshing = true
                    viewModel.changeParam(--shuffle)
                    fab.setImageResource(R.drawable.ic_shuffle_black)
                    showLongToast(requireContext(), "Diurutkan dari terbaru")
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        Log.i("190401", "onScrollStateChanged: DRAGGING")
                        if (!isDetached) {
                            handler.removeCallbacksAndMessages(null)
                            (activity as? MainActivity)?.floatingActionButton?.let {
                                it.animate()
                                    .setDuration(200)
                                    .scaleX(0f)
                                    .scaleY(0f)
                                    .alpha(0f)
                                    .withEndAction {
                                        (activity as? MainActivity)?.floatingActionButton?.isVisible =
                                            false
                                    }
                            }
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        Log.i("190401", "onScrollStateChanged: IDLE")
                        if (!isDetached) {
                            handler.postDelayed({
                                (activity as? MainActivity)?.floatingActionButton?.let {
                                    it.isVisible = true
                                    it.alpha = 1f
                                    it.animate()
                                        .setDuration(200)
                                        .scaleY(1f)
                                        .scaleX(1f)
                                }
                            }, 3000)
                        }
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        Log.i("190401", "onScrollStateChanged: SETTLING")
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        swipeRefreshLayout.startRefreshing()

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

        viewModel.initVideoUpdateFragment()
        observeFollowResult()
        observeUnfollowResult()
        observeWatchLaterResult()
        observeGetAllVideoResult()
    }

    override fun onResume() {
        super.onResume()

        val position =
            PreferenceManager.getDefaultSharedPreferences(context).getInt("UPDATE_SCROLL", 0)
        recyclerView.scrollToPosition(position)
    }

    override fun onPause() {
        super.onPause()
        Log.i("190401", "onPause: !!!!")
        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null) {
            val position =
                (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt("UPDATE_SCROLL", position)
                .apply()
        }

        handler.removeCallbacksAndMessages(null)
    }

    private fun refresh() {
        viewModel.refreshAllVideo()
    }

    companion object {
        fun newInstance(): Fragment = VideoUpdateFragment()
    }

    private fun observeGetAllVideoResult() {
        viewModel.videoUpdatePagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.networkStatusLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
                        ERROR_EMPTY_LIST -> showLongToast(
                            context,
                            "Anda belum mengikuti kanal manapun"
                        )
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                context,
                                message = getString(R.string.error_connection),
                                positive = "Coba Lagi",
                                positiveCallback = { refresh() }
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

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast(context, "Berhasil mengikuti")
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

    private fun observeUnfollowResult() {
        viewModel.unfollowChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
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

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast(context, "Berhasil disimpan")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(context, message)
                    }
                }
            }
        })
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

}
