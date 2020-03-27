package id.islaami.playmi.ui.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.islaami.playmi.R
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.ui.adapter.PlaylistSelectAdapter
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.channel.ChannelDetailActivity
import id.islaami.playmi.util.FullScreenHelper
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.fromHtmlToSpanned
import id.islaami.playmi.util.ui.*
import id.islaami.playmi.util.value
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.add_new_playlist_dialog.view.*
import kotlinx.android.synthetic.main.playlist_bottom_sheet.view.*
import kotlinx.android.synthetic.main.video_detail_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class VideoDetailActivity : BaseActivity() {
    private val viewModel: VideoViewModel by viewModel()

    private val fullScreenHelper = FullScreenHelper(this)

    lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    private var videoId = 0
    private var channelId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_detail_activity)

        setupToolbar(toolbar)

        videoId = intent.getIntExtra(EXTRA_ID, 0)

        FirebaseApp.initializeApp(this)

        if (videoId == 0) {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }

                    val p = Pattern.compile("-?\\d+")
                    val m = p.matcher(deepLink.toString())
                    while (m.find()) {
                        viewModel.initVideoDetailActivity(m.group().toInt())
                    }
                }
                .addOnFailureListener(this) { e -> Log.d("HEIKAMU", "getDynamicLink:onFailure", e) }
        } else {
            viewModel.initVideoDetailActivity(videoId)
        }

        observeVideoDetail()
        observePlaylist()

        observeFollowResult()
        observeUnfollowResult()
        observeWatchLaterResult()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
    }

    private fun refresh() {
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
            viewModel.addToPlaylist(this.videoId, playlistSelectAdapter.getSelectedId())
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showAddNewPlaylistDialog(videoId: Int) {
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
        initVideoPlayer(videoID.toString())

        channelId = channel?.ID.value()

        videoTitle.text = title.toString()
        videoDescription.apply {
            text = description.fromHtmlToSpanned()
            movementMethod = LinkMovementMethod.getInstance()
        }

        videoViews.text = "${views}x"
        videoPublishedDate.apply {
            val days = differenceInDays(publishedAt.toString())
            text = if (days == 0L) {
                "Hari ini"
            } else {
                "$days hari yang lalu"
            }
        }
        channelName.text = channel?.name.toString()
        channelPhoto.loadImage(channel?.thumbnail)
        channelFollower.text = "${channel?.followers.toString()} pengikut"
        subcategoryName.text = subcategory?.name

        /*btnNotif.apply {
            setOnClickListener {
                this.setImageResource(R.drawable.ic_notifications_active)
            }
        }*/

        btnChannel.setOnClickListener {
            ChannelDetailActivity.startActivity(
                this@VideoDetailActivity,
                channel?.name.toString(),
                channel?.ID.value()
            )
        }

        btnFollow.setOnClickListener { viewModel.followChannel(channelId) }

        btnUnfollow.setOnClickListener { viewModel.unfollowChannel(channelId) }

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
//                            createDynamicLink("$title-$description", this@showContent)
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id"))
        val videoDate = dateFormat.parse(datePublished)
        val today = Date()

        val difference = (today.time - videoDate.time) / (1000 * 3600 * 24)

        return difference
    }


    override fun onConfigurationChanged(newConfiguration: Configuration) {
        super.onConfigurationChanged(newConfiguration)
        videoPlayer.getPlayerUiController().getMenu()!!.dismiss()
    }

    private fun initVideoPlayer(videoID: String) {
        videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoID, 0f)

                addFullScreenListenerToPlayer()
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                super.onError(youTubePlayer, error)
                showSnackbar("Terjadi kesalahan pada sistem. Silahkan coba lagi.")
            }
        })
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

    private fun createDynamicLink(body: String, video: Video) {
        var shortLink: Uri?

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://islaami.id/videos/" + video.ID))
            .setDomainUriPrefix("https://playmi.page.link")
            // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("id.islaami.playmi")
                    .build()
            )
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
    }

    private fun observePlaylist() {
        viewModel.getPlaylistsResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })

        observeCreatePlaylistResult()
        observeAddToPlaylistResult()
    }

    private fun observeCreatePlaylistResult() {
        viewModel.createPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil membuat daftar putar")
                    viewModel.getPlaylists()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showSnackbar("Berhasil disimpan")
                }
                ERROR -> {
                    showSnackbar(result.message)
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
                    showSnackbarWithUndo("Berhasil mengikuti")
                    { viewModel.unfollowChannel(channelId) }

                    btnFollow.setVisibilityToGone()
                    btnUnfollow.setVisibilityToVisible()
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
                    showSnackbarWithUndo("Anda berhenti mengikuti kanal ini")
                    { viewModel.followChannel(channelId) }

                    btnUnfollow.setVisibilityToGone()
                    btnFollow.setVisibilityToVisible()
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }
}
