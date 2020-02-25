package com.example.playmi.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.VideoAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import kotlinx.android.synthetic.main.playlist_video_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistVideoActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_video_activity)

        val playlistId = intent.getIntExtra(EXTRA_ID, 0)

        viewModel.initPlaylistDetailActivity(playlistId)
        observeVideos()
    }

    private fun observeVideos() {
        viewModel.getPlaylistVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    videoAdapter = VideoAdapter(result.data)

                    recyclerView.adapter = videoAdapter
                    recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                }
                ERROR -> {
                    showSnackbar(result.message)
                }
            }
        })
    }

    companion object {
        private val EXTRA_ID = "EXTRA_ID"

        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(
                Intent(context, PlaylistVideoActivity::class.java)
                    .putExtra(EXTRA_ID, id)
            )
        }
    }
}
