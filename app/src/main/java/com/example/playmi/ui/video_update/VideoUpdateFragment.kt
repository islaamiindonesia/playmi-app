package com.example.playmi.ui.video_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmi.R
import com.example.playmi.data.model.video.Video
import com.example.playmi.ui.adapter.VideoPagedAdapter
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus
import com.example.playmi.util.ResourceStatus.*
import kotlinx.android.synthetic.main.custom_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoUpdateFragment : BaseFragment() {
    private val viewModel: VideoUpdateViewModel by viewModel()

    lateinit var videoPagedAdapter: VideoPagedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_update_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        videoPagedAdapter = VideoPagedAdapter(context)

        viewModel.initFollowingFragment()
        observeGetAllVideoResult()
    }

    private fun observeGetAllVideoResult() {
        // observe login result
        viewModel.getVideoPagedListResultLd.observe(viewLifecycleOwner, Observer { result ->
            /*investmentAdapter = InvestmentAdapter { investmentStatus, investmentId, _ ->
                if (investmentStatus != null && investmentStatus == 1) {
                    InvestmentDetailActivity.startActivity(context, investmentId)
                } else {
                    CustomDialogFragment.show(
                        fragmentManager,
                        getString(R.string.investment_not_available),
                        null,
                        "OK"
                    )
                }
            }

            investmentAdapter.add(result)

            rvInvestment.adapter = investmentAdapter
            rvInvestment.layoutManager = LinearLayoutManager(context)*/

            setupRecyclerView(result)
        })

        viewModel.getVideoPagedListNetworkStatusLd.observe(
            viewLifecycleOwner,
            Observer { result ->
                when (result?.status) {
                    LOADING -> {
                    }
                    SUCCESS -> {
                    }
                    ERROR -> {
                    }
                }
            })
    }

    private fun setupRecyclerView(result: PagedList<Video>?) {
//        recyclerView.adapter = videoPagedAdapter.apply { addVideoList(result) }
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        fun newInstance(): Fragment = VideoUpdateFragment()
    }
}
