package com.example.playmi.ui.channel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.adapter.VideoAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.ui.setupToolbar
import id.co.badr.commerce.mykopin.util.ui.*
import kotlinx.android.synthetic.main.channel_detail_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelDetailActivity : BaseActivity() {
    private val viewModel: ChannelViewModel by viewModel()

    var name: String? = null
    var channelID = 0

    private var channel: Channel? = null
    private var videos: List<Video> = emptyList()

    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_detail_activity)

        setupToolbar(toolbar)

        name = intent.getStringExtra(CHANNEL_NAME)
        channelID = intent.getIntExtra(CHANNEL_ID, 0)

        viewModel.initChannelDetail(channelID)
        observeHideStatus()
        observeFollowStatus()
        observeChannelDetail()

        observeHideResult()
        observeFollowResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                refresh()
            }
        }
    }

    private fun setupData() = with(channel) {
        if (this != null && !videos.isNullOrEmpty()) {
            channelImage.loadImage(thumbnail.toString())
            channelName.text = name
            channelDescription.text = bio.toString()
            videoCount.text = videos.size.toString()
            followerCount.text = followers.toString()

            videoAdapter = VideoAdapter(videos,
                popMenu = { context, view, video ->
                    PopupMenu(context, view).apply {
                        inflate(R.menu.menu_popup_channel_vid)

                        // if already follow channel then startFollow -> stopFollow
                        /*setOnMenuItemClickListener { item ->
                            optionMenuClickHandler(item.itemId, video.ID)
                        }*/

                        show()
                    }
                })
            recyclerView.adapter = videoAdapter
            recyclerView.layoutManager =
                LinearLayoutManager(this@ChannelDetailActivity, LinearLayoutManager.VERTICAL, false)
        } else {
            showAlertDialog(
                "Mohon maaf terjadi kesalahan, silahkan coba sesaat lagi.",
                "OK"
            ) {
                it.dismiss()
                onBackPressed()
            }
        }
    }

    private fun refresh() {
        viewModel.getChannelDetail(channelID)
        viewModel.getChannelVideos(channelID)
        viewModel.getHideStatus(channelID)
        viewModel.getFollowStatus(channelID)
    }

    private fun observeChannelDetail() {
        viewModel.channelDetailResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    channel = result.data

                    observeChannelVideos()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showAlertDialog(
                        message = result.message.toString(),
                        btnText = "OK",
                        btnCallback = {
                            it.dismiss()
                            onBackPressed()
                        })
                }
            }
        })
    }

    private fun observeChannelVideos() {
        viewModel.channelVideosResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    videos = result.data ?: emptyList()

                    setupData()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showAlertDialog(
                        message = result.message.toString(),
                        btnText = "OK",
                        btnCallback = {
                            it.dismiss()
                            onBackPressed()
                        })
                }
            }
        })
    }

    private fun observeHideStatus() {
        viewModel.hideStatusResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    val isHidden = result.data ?: false

                    if (isHidden) {
                        showAlertDialog(
                            message = "Kanal ini telah Anda sembunyikan. Silahkan ubah pada halaman Kanal yang Disembunyikan",
                            btnText = "OK",
                            btnCallback = {
                                it.dismiss()
                                onBackPressed()
                            })
                    } else {
                        btnHide.setOnClickListener {
                            showAlertDialogWith2Buttons(
                                message = "Anda tidak akan melihat seluruh video dari kanal ini\nApakah ingin menyembunyikan $name?",
                                positiveText = "Ya",
                                negativeText = "Tidak",
                                positiveCallback = {
                                    viewModel.hideChannel(channelID)
                                    it.dismiss()
                                },
                                negativeCallback = {
                                    it.dismiss()
                                }
                            )
                        }
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeFollowStatus() {
        viewModel.followStatusResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    val isFollow = result.data ?: false

                    if (isFollow) {
                        btnFollow.setVisibilityToGone()
                        btnUnfollow.apply {
                            setVisibilityToVisible()
                            setOnClickListener {
                                viewModel.unfollowChannel(channelID)
                            }
                        }
                    } else {
                        btnUnfollow.setVisibilityToGone()
                        btnFollow.apply {
                            setVisibilityToVisible()
                            setOnClickListener {
                                viewModel.followChannel(channelID)
                            }
                        }
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(this, Observer { result ->
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
        viewModel.followChannelResultLd.observe(this, Observer { result ->
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

    companion object {
        private val CHANNEL_NAME = "CHANNEL_NAME"
        private val CHANNEL_ID = "CHANNEL_ID"

        fun startActivity(context: Context?, channelName: String, channelID: Int) {
            context?.startActivity(
                Intent(context, ChannelDetailActivity::class.java)
                    .putExtra(CHANNEL_NAME, channelName)
                    .putExtra(CHANNEL_ID, channelID)
            )
        }
    }
}
