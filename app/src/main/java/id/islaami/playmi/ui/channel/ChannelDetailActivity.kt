package id.islaami.playmi.ui.channel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.data.model.channel.Channel
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ERROR_EMPTY_LIST
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.channel_detail_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelDetailActivity : BaseActivity() {
    val viewModel: ChannelViewModel by viewModel()

    lateinit var videoAdapter: VideoPagedAdapter

    var name: String? = null
    var channelID = 0

    var channel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_detail_activity)

        setupToolbar(toolbar)

        name = intent.getStringExtra(CHANNEL_NAME)
        channelID = intent.getIntExtra(CHANNEL_ID, 0)

        viewModel.initChannelDetail(channelID)
        observeChannelDetail()
        observeWatchLaterResult()
        observeFollowResult()
        observeHideResult()

        videoAdapter = VideoPagedAdapter(this,
            popMenu = { context, menuView, video ->
                PopupMenu(context, menuView).apply {
                    inflate(R.menu.menu_popup_channel_detail)

                    if (video.isSavedLater != true) {
                        menuView.setVisibilityToVisible()
                        menu.getItem(0).title = "Simpan ke Tonton Nanti"
                    } else {
                        menuView.setVisibilityToGone()
                    }

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.popSaveLater -> {
                                context.showAlertDialogWith2Buttons(
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
                            else -> false
                        }
                    }

                    show()
                }
            })

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }

        btnFollow.setOnClickListener { viewModel.followChannel(channelID) }

        btnUnfollow.setOnClickListener { viewModel.unfollowChannel(channelID) }

        btnHide.setOnClickListener { viewModel.hideChannel(channelID) }
    }

    private fun Channel.setupData() {
        channelImage.loadImage(thumbnail)
        channelName.text = name
        channelDescription.text = bio
        videoCount.text = videos.toString()
        followerCount.text = followers.toString()

        if (isFollowed == true) {
            btnUnfollow.setVisibilityToVisible()
            btnFollow.setVisibilityToGone()
        } else {
            btnFollow.setVisibilityToVisible()
            btnUnfollow.setVisibilityToGone()
        }
    }

    private fun refresh() {
        viewModel.getChannelDetail(channelID)
        viewModel.refreshAllVideo()
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

    private fun observeChannelDetail() {
        viewModel.channelDetailResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    channel = result.data ?: Channel()
                    channel?.setupData()
                    observeGetAllVideoResult()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        "CHANNEL_BLACKLISTED" -> {
                            showAlertDialog(
                                message = getString(R.string.message_channel_hide_with_guide),
                                btnText = "OK",
                                btnCallback = {
                                    it.dismiss()
                                    onBackPressed()
                                })
                        }
                        else -> {
                            handleApiError(result.message)
                            { message ->
                                showAlertDialog(
                                    message = message,
                                    btnText = "OK",
                                    btnCallback = {
                                        it.dismiss()
                                        onBackPressed()
                                    })
                            }
                        }
                    }
                }
            }
        })
    }

    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            recyclerView.adapter = videoAdapter.apply { addVideoList(result) }
            recyclerView.layoutManager =
                LinearLayoutManager(this@ChannelDetailActivity, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.pagedListNetworkStatusLd.observe(
            this,
            Observer { result ->
                when (result?.status) {
                    LOADING -> {
                        swipeRefreshLayout.startRefreshing()
                        recyclerView.setVisibilityToInvisible()
                    }
                    SUCCESS -> {
                        swipeRefreshLayout.stopRefreshing()
                        recyclerView.setVisibilityToVisible()
                    }
                    ERROR -> {
                        swipeRefreshLayout.stopRefreshing()
                        when (result.message) {
                            ERROR_EMPTY_LIST -> recyclerView.setVisibilityToInvisible()
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
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil simpan ke Tonton Nanti")
                }
                ERROR -> {
                    handleApiError(result.message)
                    { showSnackbar(it) }
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Kanal telah disembunyikan")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message)
                    { showSnackbar(it) }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    if (result.data == true) showShortToast("Berhasil mengikuti")
                    else showShortToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message)
                    { showSnackbar(it) }
                }
            }
        })
    }
}
