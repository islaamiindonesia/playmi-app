package id.islaami.playmi.ui.video

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
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
import kotlinx.android.synthetic.main.video_detail_activity.*
import kotlinx.android.synthetic.main.video_search_activity.*
import kotlinx.android.synthetic.main.video_search_activity.recyclerView
import kotlinx.android.synthetic.main.video_search_activity.swipeRefreshLayout
import kotlinx.android.synthetic.main.video_search_activity.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoSearchActivity(var searchQuery: String = "") : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_home)

                if (video.channel?.isFollowed != true) menu.getItem(1).title = "Mulai Mengikuti"
                else menu.getItem(1).title = "Berhenti Mengikuti"

                menu.getItem(0).title = "Simpan ke ic_watch Nanti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popWatchLater -> {
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
                        R.id.popFollow -> {
                            if (video.channel?.isFollowed != true) {
                                context.showAlertDialogWith2Buttons(
                                    "Apakah ingin mulai mengikuti ${video.channel?.name}?",
                                    "Iya",
                                    "Batal",
                                    positiveCallback = {
                                        viewModel.followChannel(video.channel?.ID.value())
                                        it.dismiss()
                                    },
                                    negativeCallback = { it.dismiss() })
                            } else {
                                context.showAlertDialogWith2Buttons(
                                    "Apakah ingin berhenti mengikuti ${video.channel?.name}?",
                                    "Iya",
                                    "Batal",
                                    positiveCallback = {
                                        viewModel.unfollowChannel(video.channel?.ID.value())
                                        it.dismiss()
                                    },
                                    negativeCallback = { it.dismiss() })
                            }

                            true
                        }
                        R.id.popHide -> {
                            context.showAlertDialogWith2Buttons(
                                "Anda tidak akan melihat seluruh video dari kanal ini" +
                                        "\nApakah ingin berhenti mengikuti ${video.channel?.name}?",
                                "Iya",
                                "Batal",
                                positiveCallback = {
                                    viewModel.hideChannel(video.channel?.ID.value())
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_search_activity)

        setupToolbar(toolbar)

        viewModel.initSearchableActivity()
        handleIntent()

        observeWatchLaterResult()
        observeFollowResult()
        observeUnfollowResult()
        observeHideResult()
        observeGetAllVideoResult()

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
            queryHint = "Cari Video"
            setQuery(this@VideoSearchActivity.searchQuery, false)
        }

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }

        swipeRefreshLayout.startRefreshing()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the options menu from XML
        menuInflater.inflate(R.menu.menu_search, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    private fun handleIntent() {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                this.searchQuery = query
                viewModel.getAllVideo(query)
            }
        }
    }

    private fun refresh() {
        viewModel.refreshAllVideo()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, VideoSearchActivity::class.java))
        }
    }

    /* OBSERVER */
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.pagedListNetworkStatusLd.observe(
            this,
            Observer { result ->
                when (result?.status) {
                    LOADING -> {
                    }
                    SUCCESS -> {
                        swipeRefreshLayout.stopRefreshing()
                    }
                    ERROR -> {
                        swipeRefreshLayout.stopRefreshing()
                        when (result.message) {
                            ERROR_EMPTY_LIST -> showSnackbar("Tidak ada video")
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
                    showSnackbar("Berhasil disimpan")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil Mengikuti")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeUnfollowResult() {
        viewModel.unfollowResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Anda berhenti mengikuti kanal ini")
                    refresh()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideResultLd.observe(this, Observer { result ->
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
}
