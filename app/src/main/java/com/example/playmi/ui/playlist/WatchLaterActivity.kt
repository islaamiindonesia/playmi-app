package com.example.playmi.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.VideoAdapter
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import kotlinx.android.synthetic.main.watch_later_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WatchLaterActivity : BaseActivity() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_later_activity)

        viewModel.initWatchLaterActivity()
        observeVideos()
    }

    private fun observeVideos() {
        viewModel.getLaterVideoResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    /*videoAdapter = VideoAdapter(result.data,
                        optionMenuClickHandler = { itemId, videoId ->
                            when (itemId) {
                                R.id.popWatchLater -> {

                                    true
                                }
                                R.id.popStartFollow -> {
                                    *//*Toast.makeText(
                                        context,
                                        "Choose: Start Follow",
                                        Toast.LENGTH_SHORT
                                    ).show()*//*
                                    true
                                }
                                R.id.popHideChannel -> {
                                    *//*Toast.makeText(
                                        context,
                                        "Choose: Hide Channel",
                                        Toast.LENGTH_SHORT
                                    ).show()*//*
                                    true
                                }
                                else -> false
                            }
                        })*/

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
        fun startActivity(context: Context?, id: Int) {
            context?.startActivity(Intent(context, WatchLaterActivity::class.java))
        }
    }
}
