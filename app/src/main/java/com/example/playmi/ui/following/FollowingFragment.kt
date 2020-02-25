package com.example.playmi.ui.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.playmi.R
import com.example.playmi.data.model.category.Category
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.ui.home.CustomFragment
import kotlinx.android.synthetic.main.home_fragment.*

class FollowingFragment : BaseFragment() {
//    private val viewModel: VideoUpdateViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.following_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = ViewPagerAdapter()
        viewPager.offscreenPageLimit = 1
        tabLayout.setupWithViewPager(viewPager)
    }

    inner class ViewPagerAdapter() :
        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return Fragment()
//            return CustomFragment.newInstance(list[position].name.toString())
        }

        override fun getCount(): Int {
            return 0
        }

        override fun getPageTitle(position: Int): CharSequence? {
//            return list[position].name.toString()
            return ""
        }
    }

    companion object {
        fun newInstance(): Fragment = FollowingFragment()
    }
}
