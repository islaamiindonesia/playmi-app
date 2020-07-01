package id.islaami.playmi.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.adapter.AdsAdapter
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.util.ERROR_EMPTY_LIST
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.video_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class VideoCategoryFragment(
    var categoryID: Int = 0,
    var listItems: ArrayList<Any> = ArrayList(),
    var nativeAds: ArrayList<UnifiedNativeAd> = ArrayList()
) : BaseFragment() {
    private val viewModel: HomeViewModel by viewModel()

    lateinit var adLoader: AdLoader

    lateinit var adapter: AdsAdapter

    private var videoPagedAdapter = VideoPagedAdapter(context,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView, Gravity.END).apply {
                inflate(R.menu.menu_popup_home)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                menu.getItem(0).title = "Simpan ke Tonton Nanti"

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
                        R.id.popHide -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = childFragmentManager,
                                text = getString(R.string.channel_hide, video.channel?.name),
                                okCallback = { viewModel.hideChannel(video.channel?.ID.value()) }
                            )

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

        swipeRefreshLayout.startRefreshing()

        adapter = AdsAdapter()
    }

    override fun onResume() {
        super.onResume()

        viewModel.initVideoCategoryFragment(categoryID)
        observeWatchLaterResult()
        observeFollowResult()
        observeUnfollowResult()
        observeHideResult()
        observeGetAllVideoResult()
    }

    private fun refresh() {
        if (categoryID > 0) {
            viewModel.refreshAllVideoByCategory()
        } else {
            viewModel.refreshAllVideo()
        }

        (this.parentFragment as HomeFragment).refresh()
    }

    private fun setupRecyclerView(result: PagedList<Video>?) {
        recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        const val NUMBER_OF_ADS = 3
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"

        fun newInstance(id: Int) =
            VideoCategoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_CATEGORY, id)
                }
            }
    }

    private fun loadNativeAds() {
        val builder: AdLoader.Builder = AdLoader.Builder(context, getString(R.string.ad_unit_id))
        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
                // and if so, insert the ads into the list.
                nativeAds.add(unifiedNativeAd)
                if (!adLoader.isLoading) {
                    insertAdsInMenuItems()
                }
            }.withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e(
                            "MainActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another."
                        )
                        if (!adLoader.isLoading) {
                            insertAdsInMenuItems()
                        }
                    }
                }).build()

        // Load the Native Express ad.
        adLoader.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)
    }

    private fun insertAdsInMenuItems() {
        if (nativeAds.size <= 0) {
            return
        }
        val offset: Int = listItems.size / nativeAds.size.plus(1)
        var index = 0
        for (ad in nativeAds) {
            listItems.add(index, ad)
            index += offset
        }
    }


    /* OBSERVERS */
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
//            setupRecyclerView(result)

            listItems.addAll(result)

            loadNativeAds()

//            recyclerView.adapter = adapter.apply { addList(listItems) }
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.pagedListNetworkStatusLd.observe(
            viewLifecycleOwner,
            Observer { result ->
                when (result?.status) {
                    LOADING -> {
                    }
                    SUCCESS -> {
                        swipeRefreshLayout.stopRefreshing()
                        recyclerView.setVisibilityToVisible()
                        emptyText.setVisibilityToGone()
                    }
                    ERROR -> {
                        swipeRefreshLayout.stopRefreshing()
                        when (result.message) {
                            ERROR_EMPTY_LIST -> {
                                emptyText.setVisibilityToVisible()
                                recyclerView.setVisibilityToGone()
                            }
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
        viewModel.watchLaterResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    context?.showShortToast("Berhasil disimpan")
                    refresh()
                }
                ERROR -> {
                    context?.showShortToast(result.message)
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
                    context?.showShortToast("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    context?.showShortToast(result.message)
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
                    context?.showShortToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    context?.showShortToast(result.message)
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    context?.showShortToast(getString(R.string.message_channel_hide))
                    refresh()
                }
                ERROR -> {
                    context?.showShortToast(result.message)
                }
            }
        })
    }
}
