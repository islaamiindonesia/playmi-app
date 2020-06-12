package id.islaami.playmi.ui.channel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ERROR_EMPTY_LIST
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.channel_video_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelVideoActivity : BaseActivity() {
    val viewModel: ChannelViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_channel_detail)

                if (video.isSavedLater != true) {
                    menuView.setVisibilityToVisible()
                    menu.getItem(0).title = "Simpan ke ic_watch Nanti"
                } else {
                    menuView.setVisibilityToGone()
                }

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popSaveLater -> {
                            context.showAlertDialogWith2Buttons(
                                "Simpan ke daftar ic_watch Nanti?",
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

    var channelID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_video_activity)

        setupToolbar(toolbar)

        channelID = intent.getIntExtra(CHANNEL_ID, 0)

        viewModel.initChannelVideo(channelID)
        observeGetAllVideoResult()
        observeWatchLaterResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
    }

    private fun refresh() {
        viewModel.refreshAllVideo()
    }

    companion object {
        private val CHANNEL_NAME = "CHANNEL_NAME"
        private val CHANNEL_ID = "CHANNEL_ID"

        fun startActivity(context: Context, channelID: Int) {
            context.startActivity(
                Intent(context, ChannelVideoActivity::class.java).putExtra(CHANNEL_ID, channelID)
            )
        }
    }

    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.pagedListNetworkStatusLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_EMPTY_LIST -> showSnackbar("Berhasil")
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
                    showSnackbar("Berhasil simpan ke ic_watch Nanti")
                }
                ERROR -> {
                    handleApiError(result.message)
                    { showSnackbar(it) }
                }
            }
        })
    }
}
