package id.islaami.playmi2021.ui.video

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi2021.ui.base.BaseVideoActivity
import id.islaami.playmi2021.ui.channel.ChannelDetailActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.video_detail_activity.*
import kotlinx.android.synthetic.main.video_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoDetailActivity(
    var videoId: Int = 0,
    var channelId: Int = 0
) : BaseVideoActivity() {
    private val viewModel: VideoViewModel by viewModel()

    lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_detail_activity)

        window.setFlags(
            FLAG_SECURE,
            FLAG_SECURE
        )

        videoId = intent.getIntExtra(EXTRA_ID, 0)

        FirebaseApp.initializeApp(this)

        // videoId will have 0 value if user opens video from external/shared url
        if (videoId == 0) {
            val uri = Uri.parse(intent.data.toString())
            viewModel.initVideoDetailActivity(uri.lastPathSegment.toString().toInt())
        } else {
            viewModel.initVideoDetailActivity(videoId)
        }

        observeVideoDetail()
        observeFollowResult()
        observeUnfollowResult()
        observeWatchLaterResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { refresh() }
        }

        swipeRefreshLayout.startRefreshing()

        lifecycle.addObserver(videoPlayer)
    }

    override fun onBackPressed() {
        if (videoPlayer.isFullScreen()) {
            videoPlayer.exitFullScreen()
        } else {
            finish()
        }
    }

    private fun refresh() {
        viewModel.getPlaylists()
        viewModel.getVideoDetail(videoId)
    }

    private fun showBottomSheet(videoId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.playlist_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialogView.btnNew.setOnClickListener {
            showAddNewPlaylistDialog(videoId)
            dialog.dismiss()
        }
        dialogView.rvPlaylist.apply {
            adapter = playlistSelectAdapter
            layoutManager = LinearLayoutManager(this@VideoDetailActivity)
        }
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnSave.setOnClickListener {
            viewModel.addToManyPlaylists(videoId, playlistSelectAdapter.selectedIds)
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showAddNewPlaylistDialog(videoId: Int) {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.PlaymiMaterialDialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.background = getDrawable(R.drawable.bg_dialog)
        }

        val dialogView = layoutInflater.inflate(R.layout.add_new_playlist_dialog, null)
        alertDialog.setView(dialogView)
        alertDialog.setPositiveButton("SIMPAN") { dialogInterface, _ ->
            viewModel.createPlaylist(dialogView.playlistName.text.toString(), videoId)
            dialogInterface.dismiss()
        }
        alertDialog.setNegativeButton("BATAL") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val dialog = alertDialog.create()
        dialog.show()

        val positiveBtn = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        positiveBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))

        val negativeBtn = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))
    }

    private fun Video.showContent() {
        initVideoPlayer(videoID.toString())

        channelId = channel?.ID.value()

        videoTitle.text = title.toString()
        videoDescription.apply {
            text = description.fromHtmlToSpanned()
            movementMethod = LinkMovementMethod.getInstance()
        }

        videoViews.text = "${views}x"

        videoPublishedDate.text = publishedAt.fromDbFormatDateTimeToCustomFormat("dd MMMM yyyy")

        channelName.text = channel?.name.toString()
        channelPhoto.loadImage(channel?.thumbnail)
        channelFollower.text = "${channel?.followers.toString()} pengikut"
        subcategoryName.apply {
            text = subcategory?.name
            setOnClickListener {
                VideoSubcategoryActivity.startActivity(
                    this@VideoDetailActivity,
                    category?.ID.value(),
                    subcategory?.ID.value(),
                    subcategory?.name.toString()
                )
            }
        }
        verified_icon.isVisible = channel?.status == 1
        serial.isVisible = seriesId != null
        serial_name.text = seriesName
        serial_name.setOnClickListener {
            VideoSeriesActivity.startActivity(
                this@VideoDetailActivity,
                seriesId.value(),
                seriesName.toString()
            )
        }

        btnChannel.setOnClickListener {
            ChannelDetailActivity.startActivity(
                this@VideoDetailActivity,
                channel?.name.toString(),
                channel?.ID.value()
            )
        }

        btnFollow.setOnClickListener {
            if (channel?.isBlacklisted == true) {
                PlaymiDialogFragment.show(
                    fragmentManager = supportFragmentManager,
                    text = "Kanal sedang Anda sembunyikan, untuk bisa mengikuti silakan tampilkan dulu kanal ini",
                    okCallback = {
                        ChannelDetailActivity.startActivity(
                            this@VideoDetailActivity,
                            channel?.name.toString(),
                            channel?.ID.value()
                        )
                    },
                    okText = "Tampilkan"
                )
            } else {
                viewModel.followChannel(channelId)
            }

        }

        btnUnfollow.setOnClickListener {
            PlaymiDialogFragment.show(
                fragmentManager = supportFragmentManager,
                text = getString(R.string.channel_unfollow, channel?.name),
                okCallback = {
                    viewModel.unfollowChannel(channelId)
                }
            )
        }

        if (channel?.isFollowed == true) {
            btnUnfollow.setVisibilityToVisible()
            btnFollow.setVisibilityToGone()
        } else {
            btnUnfollow.setVisibilityToGone()
            btnFollow.setVisibilityToVisible()
        }

        menu.setOnClickListener {
            PopupMenu(this@VideoDetailActivity, it).apply {
                inflate(R.menu.menu_popup_video_detail)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.popSaveLater -> {
                            viewModel.watchLater(ID.value())
                            true
                        }
                        R.id.popSavePlaylist -> {
                            showBottomSheet(ID.value())
                            true
                        }
                        R.id.popShare -> {
                            createDynamicLink("${channel?.name}-$title", this@showContent)
                            true
                        }
                        else -> false
                    }
                }

                show()
            }
        }
    }

    override fun onConfigurationChanged(newConfiguration: Configuration) {
        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoPlayer.enterFullScreen()
        }
        super.onConfigurationChanged(newConfiguration)
    }

    private fun initVideoPlayer(videoID: String) {
        videoPlayer.enableBackgroundPlayback(false)
        videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadOrCueVideo(lifecycle, videoID, 0f)
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                super.onError(youTubePlayer, error)
                showSnackbar("Terjadi kesalahan pada sistem. Silahkan coba lagi.")
            }
        })
        videoPlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.hide(WindowInsets.Type.statusBars())
                } else {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                }
            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.show(WindowInsets.Type.statusBars())
                } else {
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                }
            }

        })

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoPlayer.enterFullScreen()
        }
    }


    private fun createDynamicLink(body: String, video: Video) {
        var shortLink: Uri?

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://islaami.id/videos/" + video.ID))
            .setDomainUriPrefix("https://videoplaymi.page.link")
            /*// Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("id.islaami.playmi")
                    .build()
            )*/
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(video.title.toString())
                    .setDescription(video.description.toString())
                    .setImageUrl(Uri.parse(video.thumbnail))
                    .build()
            )
            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
            .addOnSuccessListener { result ->
                // Short link created
                shortLink = result.shortLink

                startActivity(
                    Intent.createChooser(Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$body\n$shortLink")
                        type = "text/plain"
                    }, "Bagikan")
                )
            }.addOnFailureListener {
                Log.d("HEIKAMU", it.toString())
            }
    }

    companion object {
        const val EXTRA_ID = "EXTRA_ID"

        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(
                Intent(context, VideoDetailActivity::class.java).putExtra(EXTRA_ID, id)
            )
        }
    }

    private fun observeVideoDetail() {
        viewModel.getVideoResultLd.observe(this, { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    result.data?.showContent()
                    observePlaylist()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
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

    private fun observePlaylist() {
        viewModel.getPlaylistsResultLd.observe(this, { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    when (result.message) {
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
                        else -> handleApiError(errorMessage = result.message) { showLongToast(it) }
                    }
                }
            }
        })

        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
    }

    private fun observeCreatePlaylistResult() {
        viewModel.createPlaylistResultLd.observe(this, { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil membuat daftar putar")
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) {
                        showLongToast(it)
                    }
                }
            }
        })
    }

    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResultLd.observe(this, { result ->
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

    private fun observeWatchLaterResult() {
        viewModel.watchLaterResultLd.observe(this, { result ->
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
        viewModel.followResultLd.observe(this, { result ->
            when (result?.status) {
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
        viewModel.unfollowResultLd.observe(this, { result ->
            when (result?.status) {
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
}
