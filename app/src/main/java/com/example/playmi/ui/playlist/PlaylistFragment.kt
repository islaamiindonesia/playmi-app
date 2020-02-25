package com.example.playmi.ui.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.ui.adapter.PlaylistSelectAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus.*
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToGone
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToVisible
import id.co.badr.commerce.mykopin.util.ui.startRefreshing
import id.co.badr.commerce.mykopin.util.ui.stopRefreshing
import kotlinx.android.synthetic.main.playlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment() {
    private val viewModel: PlaylistViewModel by viewModel()

    lateinit var playlistSelectAdapter: PlaylistSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.initPlaylistFragment()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeWatchLaterAmount()
    }

    private fun observeWatchLaterAmount() {
        viewModel.getWatchLaterAmountLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    Log.d("HEIKAMU", "${result.data} Video")
                    watchLaterAmount.text = "${result.data} Video"
                    observePlaylist()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                }
            }
        })
    }

    private fun observePlaylist() {
        viewModel.getPlaylistResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    playlistSelectAdapter = PlaylistSelectAdapter(result.data)

                    recyclerView.adapter = playlistSelectAdapter
                    recyclerView.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                }
            }
        })
    }

    companion object {
        fun newInstance(): Fragment = PlaylistFragment()
    }
}
