package id.islaami.playmi.ui.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.ui.adapter.VideoPagedAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.*
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.video_category_fragment.*
import kotlinx.android.synthetic.main.video_label_activity.*
import kotlinx.android.synthetic.main.video_label_activity.recyclerView
import kotlinx.android.synthetic.main.video_label_activity.swipeRefreshLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoLabelActivity(
    var categoryID: Int = 0,
    var subcategoryID: Int = 0,
    var labelID: Int = 0
) : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private var videoPagedAdapter = VideoPagedAdapter(this,
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
                                fragmentManager = supportFragmentManager,
                                text = "Simpan ke daftar Tonton Nanti?",
                                okCallback = { viewModel.watchLater(video.ID.value()) }
                            )

                            true
                        }
                        R.id.popFollow -> {
                            if (video.channel?.isFollowed != true) {
                                PlaymiDialogFragment.show(
                                    fragmentManager = supportFragmentManager,
                                    text = getString(R.string.channel_follow, video.channel?.name),
                                    okCallback = { viewModel.followChannel(video.channel?.ID.value()) }
                                )
                            } else {
                                PlaymiDialogFragment.show(
                                    fragmentManager = supportFragmentManager,
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
                                fragmentManager = supportFragmentManager,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_label_activity)

        setupToolbar(toolbar, title = intent.getStringExtra(LABEL_NAME))

        categoryID = intent.getIntExtra(CATEGORY_ID, 0)
        subcategoryID = intent.getIntExtra(SUBCATEGORY_ID, 0)
        labelID = intent.getIntExtra(LABEL_ID, 0)

        viewModel.initVideoLabelActivity(categoryID, subcategoryID, labelID)

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.getAllVideoByLabel(categoryID, subcategoryID, labelID)
    }

    override fun onResume() {
        super.onResume()

        swipeRefreshLayout.startRefreshing()

        observeGetAllVideoResult()
        observeWatchLaterResult()
        observeFollowResult()
        observeUnfollowResult()
        observeHideResult()

        val position =
            PreferenceManager.getDefaultSharedPreferences(this).getInt("LABEL_SCROLL", 0)
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
                .putInt("LABEL_SCROLL", position)
                .apply()
        }
    }

    private fun refresh() {
        viewModel.refreshAllVideoBySub()
    }

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SUBCATEGORY_ID = "SUBCATEGORY_ID"
        const val LABEL_ID = "LABEL_ID"
        const val LABEL_NAME = "LABEL_NAME"

        fun startActivity(
            context: Context?,
            categoryId: Int,
            subcategoryId: Int,
            labelId: Int,
            labelName: String
        ) {
            context?.startActivity(Intent(context, VideoLabelActivity::class.java).apply {
                putExtra(CATEGORY_ID, categoryId)
                putExtra(SUBCATEGORY_ID, subcategoryId)
                putExtra(LABEL_ID, labelId)
                putExtra(LABEL_NAME, labelName)
            })
        }
    }

    /* OBSERVERS */
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
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            showMaterialAlertDialog(
                                getString(R.string.error_connection_timeout),
                                "Coba Lagi",
                                positiveCallback = { refresh() },
                                dismissCallback = { refresh() }
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

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil disimpan")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeUnfollowResult() {
        viewModel.unfollowResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast(getString(R.string.message_channel_hide))
                    refresh()
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showLongToast(message)
                    }
                }
            }
        })
    }
}
