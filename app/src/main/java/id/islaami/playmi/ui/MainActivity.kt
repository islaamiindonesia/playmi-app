package id.islaami.playmi.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.channel_following.OrganizeChannelFragment
import id.islaami.playmi.ui.home.HomeFragment
import id.islaami.playmi.ui.playlist.PlaylistFragment
import id.islaami.playmi.ui.video_update.VideoUpdateFragment
import id.islaami.playmi.util.ui.loadImage
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        FirebaseMessaging.getInstance().subscribeToTopic("playmi")

        MobileAds.initialize(this) {
            Log.d("HEIKAMU", it.adapterStatusMap.toString())
        }

        setupTabLayout()
    }

    private fun setupTabLayout() {
        // Setup View Pager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerMain.adapter = viewPagerAdapter
        viewPagerMain.offscreenPageLimit = 4

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

        for (tabPosition in 0 until viewPagerAdapter.count) {
            val tabItem: ImageView =
                LayoutInflater.from(this).inflate(R.layout.main_tab_item, null) as ImageView

            if (tabPosition == 0) {
                tabItem.loadImage(tabIconListSelected[tabPosition])
                tabItem.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.accent))
            } else {
                tabItem.loadImage(tabIconListDefault[tabPosition])
                tabItem.setColorFilter(ContextCompat.getColor(this, R.color.main_tab))
            }
            tabLayoutMain.getTabAt(tabPosition)?.customView = tabItem
        }

        tabLayoutMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: ImageView = customView as ImageView
                    tabItem.loadImage(tabIconListDefault[position])
                    tabItem.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.main_tab
                        )
                    )
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.run {
                    val tabItem: ImageView = customView as ImageView
                    tabItem.loadImage(tabIconListSelected[position])
                    tabItem.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.accent
                        )
                    )
                    viewPagerMain.setCurrentItem(position, false)
                }
            }
        })
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val numberOfTab = 4

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> HomeFragment.newInstance()
            1 -> VideoUpdateFragment.newInstance()
            2 -> PlaylistFragment.newInstance()
            3 -> OrganizeChannelFragment.newInstance()
            else -> Fragment()
        }

        override fun getCount(): Int = numberOfTab
    }

    companion object {
        fun startActivityClearTask(context: Context?) {
            context?.startActivity(
                Intent(context, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        fun startActivityClearTop(context: Context?) {
            context?.startActivity(
                Intent(context, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
