package id.islaami.playmi2021.ui.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.adapter.VideoPagedAdapter
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.activity_video_series.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoSeriesActivity(
    var seriesId: Int = 0
) : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
        showSeries = false,
        popMenu = { context, menuView, video ->
            PopupMenu(context, menuView).apply {
                inflate(R.menu.menu_popup_channel_detail)

                menu.getItem(0).title = "Simpan ke Tonton Nanti"

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popSaveLater -> {
                            PlaymiDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = "Simpan ke daftar Tonton Nanti?",
                                okCallback = { viewModel.watchLater(video.ID.value()) }
                            )

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
        setContentView(R.layout.activity_video_series)
        
        setupToolbar(toolbar, title = intent.getStringExtra(SERIES_NAME))

        seriesId = intent.getIntExtra(SERIES_ID, 0)

        viewModel.initVideoSeriesActivity(seriesId)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }
    }
    
    override fun onResume() {
        super.onResume()

        swipeRefreshLayout.startRefreshing()

        observeGetAllVideoResult()

        val position =
            PreferenceManager.getDefaultSharedPreferences(this).getInt("SERIES_SCROLL", 0)
        recyclerView.scrollToPosition(position)
    }
    
    override fun onPause() {
        super.onPause()

        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null) {
            val position =
                (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("SERIES_SCROLL", position)
                .apply()
        }
    }

    private fun refresh() {
        viewModel.refreshAllVideoBySeries()
    }

    companion object {
        const val SERIES_ID = "SERIES_ID"
        const val SERIES_NAME = "SERIES_NAME"

        fun startActivity(
            context: Context?,
            seriesId: Int,
            seriesName: String
        ) {
            context?.startActivity(Intent(context, VideoSeriesActivity::class.java).apply {
                putExtra(SERIES_ID, seriesId)
                putExtra(SERIES_NAME, seriesName)
            })
        }
    }
    
    private fun observeGetAllVideoResult() {
        viewModel.videoPagedListResultLd.observe(this, Observer { result ->
            recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })

        viewModel.networkStatusLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    recyclerView.setVisibilityToVisible()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_EMPTY_LIST -> {
                            recyclerView.setVisibilityToGone()
                        }
                        ERROR_CONNECTION -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection),
                                "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) { showLongToast(it) }
                        }
                    }
                }
            }
        })
    }
}