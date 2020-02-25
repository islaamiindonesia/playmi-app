package com.example.playmi.ui.video

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.adapter.PlaylistSelectAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.FullScreenHelper
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.fromHtmlToSpanned
import com.example.playmi.util.ui.CustomDialogFragment
import com.example.playmi.util.value
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import id.co.badr.commerce.mykopin.util.ui.*
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.video_detail_activity.*
import kotlinx.android.synthetic.main.video_detail_bottom_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class VideoDetailActivity : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private val fullScreenHelper = FullScreenHelper(this)

    private lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    private var videoId = 0
    private var channelId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_detail_activity)

        videoId = intent.getIntExtra(EXTRA_ID, 0)

        viewModel.initVideoDetailActivity(videoId)
        observeVideoDetail()

        observeFollowResult()
        observePlaylist()
    }

    private fun refresh() {
        viewModel.getVideoDetail(videoId)
    }

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(this, Observer { result ->
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

    private fun observeVideoDetail() {
        viewModel.getVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()
                    result.data?.showContent()

                    viewModel.getFollowingStatus(channelId)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    CustomDialogFragment.show(
                        fragmentManager = supportFragmentManager,
                        text = result.message.toString(),
                        btnOk = "Coba Lagi",
                        btnCancel = "Kembali",
                        okCallback = { refresh() },
                        outsideTouchCallback = { finish() }
                    )
                }
            }
        })

        observeFollowingStatus()
        observeCreatePlaylist()
        observeVideoAddToPlaylist()
        observeWatchLaterResult()
    }

    private fun observeFollowingStatus() {
        viewModel.getFollowingStatusResultLd.observe(this, Observer { result ->
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
                            setOnClickListener { viewModel.unfollowChannel(channelId) }
                        }
                    } else {
                        btnUnfollow.setVisibilityToGone()
                        btnFollow.apply {
                            setVisibilityToVisible()
                            setOnClickListener { viewModel.followChannel(channelId) }
                        }
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    showSnackbar("Error")
                }
            }
        })
    }

    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.video_detail_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialogView.btnNew.setOnClickListener {
            showAddNewPlaylistDialog()
            dialog.dismiss()
        }
        dialogView.rvPlaylist.apply {
            adapter = playlistSelectAdapter
            layoutManager = LinearLayoutManager(this@VideoDetailActivity)
        }
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnSave.setOnClickListener {
            viewModel.addToPlaylist(videoId, playlistSelectAdapter.getSelectedId())
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showAddNewPlaylistDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_new_playlist_dialog, null)

        dialogBuilder.setView(dialogView)
            .setPositiveButton("Simpan") { dialogInterface, i ->
                viewModel.createPlaylist(dialogView.playlistName.text.toString(), videoId)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun Video.showContent() {
        initVideoPlayer(url.toString())

        channelId = channelID.value()

        videoTitle.text = title.toString()
        videoDescription.apply {
            text = description.fromHtmlToSpanned()
            movementMethod = LinkMovementMethod.getInstance()
        }
        videoViews.text = views.toString()
        videoPublishedDate.apply {
            val days = differenceInDays(publishedAt.toString())
            text = if (days == 0L) {
                "Hari ini"
            } else {
                "$days hari yang lalu"
            }
        }
        channelName.text = channel.toString()
        channelPhoto.loadImage(channelThumbnail)
        channelFollower.text = "${followers.toString()} pengikut"
        videoCategory.text = subcategory

        btnNotif.apply {
            setOnClickListener {
                this.setImageResource(R.drawable.ic_notifications_active)
            }
        }

        optionVideo.setOnClickListener {
            PopupMenu(this@VideoDetailActivity, it).apply {
                inflate(R.menu.menu_popup_video_detail)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.popSaveLater -> {
                            viewModel.watchLater(ID.value())
                            true
                        }
                        R.id.popSavePlaylist -> {
                            showBottomSheet()
                            true
                        }
                        R.id.popShare -> {
                            Toast.makeText(
                                this@VideoDetailActivity,
                                "Choose: Share",
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        else -> false
                    }
                }

                show()
            }
        }
    }

    private fun differenceInDays(datePublished: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val videoDate = dateFormat.parse(datePublished)
        val today = Date()

        val difference = (today.time - videoDate.time) / (1000 * 3600 * 24)

        return difference
    }


    override fun onConfigurationChanged(newConfiguration: Configuration) {
        super.onConfigurationChanged(newConfiguration)
        videoPlayer.getPlayerUiController().getMenu()!!.dismiss()
    }

    private fun initVideoPlayer(link: String) {
        val regex =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val matcher = Pattern.compile(regex).matcher(link)
        if (matcher.find()) {
            // The player will automatically release itself when the activity is destroyed.
            // The player will automatically pause when the activity is stopped
            // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
            lifecycle.addObserver(videoPlayer)

            videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(
                        matcher.group(),
                        0f
                    )

                    addFullScreenListenerToPlayer()
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    super.onError(youTubePlayer, error)
                    Log.d("HEIKAMU", error.name)
                }
            })
        }
    }

    private fun addFullScreenListenerToPlayer() {
        videoPlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullScreenHelper.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                fullScreenHelper.exitFullScreen()
            }
        })
    }

    private fun observePlaylist() {
        viewModel.getPlaylistsResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    playlistSelectAdapter =
                        PlaylistSelectAdapter(result.data)
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
                }
                SUCCESS -> {
                    showSnackbar("Followed")
                    viewModel.getFollowingStatus(channelId)
                }
                ERROR -> {
                    showSnackbar("Error")
                }
            }
        })

        observeFollowingStatus()
    }

    private fun observeCreatePlaylist() {
        viewModel.createPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Playlist Created")
                    viewModel.getPlaylists()
                }
                ERROR -> {
                    showSnackbar("Error")
                }
            }
        })

        observePlaylist()
    }

    private fun observeVideoAddToPlaylist() {
        viewModel.addToPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Video Was Added to Playlist")
                    viewModel.getPlaylists()
                }
                ERROR -> {
                    showSnackbar("Error")
                }
            }
        })

        observePlaylist()
    }

    companion object {
        private val EXTRA_ID = "EXTRA_ID"

        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(
                Intent(context, VideoDetailActivity::class.java).putExtra(EXTRA_ID, id)
            )
        }
    }
}
