package com.example.playmi.ui.channel_following

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.playmi.R
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.value
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.channel_following_fragment.*

class ChannelFollowingFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.channel_following_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)

        val tabLabelList = arrayOf(
            getString(R.string.following),
            getString(R.string.hidden)
        )

        val tabColorDefault = context?.let { ContextCompat.getColor(it, R.color.black_50) }
        val tabColorSelected = context?.let { ContextCompat.getColor(it, R.color.primary) }

        for (tabPosition in 0 until viewPagerAdapter.count) {
            val tabItem: TextView =
                LayoutInflater.from(context).inflate(R.layout.main_tab_item, null) as TextView

            tabItem.text = tabLabelList[tabPosition]
            tabItem.gravity = Gravity.CENTER
            if (tabPosition == 0) {
                tabItem.setTextColor(tabColorSelected.value())
            }
            tabLayout.getTabAt(tabPosition)?.customView = tabItem
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: TextView = customView as TextView
                    tabItem.setTextColor(tabColorDefault.value())
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: TextView = customView as TextView
                    tabItem.setTextColor(tabColorSelected.value())
                    viewPager.setCurrentItem(position, false)
                }
            }
        })
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) FollowingFragment.newInstance() else HiddenFragment.newInstance()
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.following) else getString(R.string.hidden)
        }
    }

    companion object {
        fun newInstance(): Fragment = ChannelFollowingFragment()
    }
}
