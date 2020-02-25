package com.example.playmi.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.playmi.R
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.ui.following.FollowingFragment
import com.example.playmi.ui.home.HomeFragment
import com.example.playmi.ui.playlist.PlaylistFragment
import com.example.playmi.ui.setting.SettingActivity
import com.example.playmi.ui.video_update.VideoUpdateFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import id.co.badr.commerce.mykopin.util.ui.setIcon
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        FirebaseMessaging.getInstance().subscribeToTopic("playmi")

        setupTabLayout()

        optionSetting.setOnClickListener {
            Log.d("HEIKAMU", "SETTING")
            SettingActivity.startActivity(this)
        }
    }

    fun setupTabLayout() {
        // Setup View Pager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerMain.adapter = viewPagerAdapter
        viewPagerMain.offscreenPageLimit = 4
        viewPagerMain.disableScroll(true)

        // Setup Tab Layout
        tabLayoutMain.setupWithViewPager(viewPagerMain)

        val tabIconListSelected = arrayOf(
            R.drawable.ic_home_selected,
            R.drawable.ic_notification_selected,
            R.drawable.ic_playlist_selected,
            R.drawable.ic_following_selected
        )

        val tabIconListDefault = arrayOf(
            R.drawable.ic_home,
            R.drawable.ic_notification,
            R.drawable.ic_playlist,
            R.drawable.ic_following
        )

        val tabColorDefault = ContextCompat.getColor(this, R.color.black)
        val tabColorSelected = ContextCompat.getColor(this, R.color.primary)

        for (tabPosition in 0 until viewPagerAdapter.count) {
            val tabItem: TextView =
                LayoutInflater.from(this).inflate(R.layout.main_tab_item, null) as TextView

            if (tabPosition == 0) {
                tabItem.setIcon(tabIconListSelected[tabPosition])
            } else {
                tabItem.setIcon(tabIconListDefault[tabPosition])
            }
            tabLayoutMain.getTabAt(tabPosition)?.customView = tabItem
        }

        tabLayoutMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: TextView = customView as TextView
                    tabItem.setTextColor(tabColorDefault)
                    tabItem.setIcon(tabIconListDefault[position])
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: TextView = customView as TextView
                    tabItem.setTextColor(tabColorSelected)
                    tabItem.setIcon(tabIconListSelected[position])
                    viewPagerMain.setCurrentItem(position, false)
                }
            }
        })
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        private val numberOfTab = 4

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> HomeFragment.newInstance()
            1 -> VideoUpdateFragment.newInstance()
            2 -> PlaylistFragment.newInstance()
            3 -> FollowingFragment.newInstance()
            else -> Fragment()
        }

        override fun getCount(): Int = numberOfTab
    }

    companion object {
        fun startActivityAfterLogin(context: Context?) {
            context?.startActivity(
                Intent(context, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        fun startActivityAfter(context: Context?) {
            context?.startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
