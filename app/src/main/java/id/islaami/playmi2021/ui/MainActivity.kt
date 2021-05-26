package id.islaami.playmi2021.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.ui.base.BaseHasFloatingButtonFragment
import id.islaami.playmi2021.ui.base.BaseRecyclerViewFragment
import id.islaami.playmi2021.ui.channel_following.OrganizeChannelFragment
import id.islaami.playmi2021.ui.home.HomeFragment
import id.islaami.playmi2021.ui.playlist.PlaylistFragment
import id.islaami.playmi2021.ui.video_update.VideoUpdateFragment
import id.islaami.playmi2021.util.ui.loadImage
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()
    private var viewPagerAdapter : ViewPagerAdapter? = null
    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // subscribe to "playmi" topic
        FirebaseMessaging.getInstance().subscribeToTopic("playmi")

        setupTabLayout()
        floatingActionButton = fab
    }

    override fun onResume() {
        super.onResume()
        viewModel.notifyOnline()
    }

    private fun setupTabLayout() {
        // Setup View Pager
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerMain.adapter = viewPagerAdapter
        viewPagerMain.offscreenPageLimit = 4

        // Setup Tab Layout
        tabLayoutMain.setupWithViewPager(viewPagerMain)

        val tabIconListSelected = arrayOf(
            R.drawable.ic_home_selected,
            R.drawable.ic_explore_selected,
            R.drawable.ic_playlist_selected,
            R.drawable.ic_following_selected
        )

        val tabIconListDefault = arrayOf(
            R.drawable.ic_home,
            R.drawable.ic_explore,
            R.drawable.ic_playlist,
            R.drawable.ic_following
        )

        // setup each tab on first load
        for (tabPosition in 0 until (viewPagerAdapter?.count ?: 0)) {
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
                val fragment = viewPagerAdapter?.getFragment(tab?.position ?: 0)
                if (fragment is BaseRecyclerViewFragment) {
                    (fragment as BaseRecyclerViewFragment).scrollToTop()
                }
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
                val fragment = viewPagerAdapter?.getFragment(tab?.position ?: 0)
                if (fragment is BaseHasFloatingButtonFragment) {
                    fab.apply {
                        isVisible = true
                        scaleX = 1f
                        scaleY = 1f
                        alpha = 1f
                    }
                } else {
                    fab.isVisible = false
                }

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
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragments = SparseArray<Fragment>()

        private val numberOfTab = 4

        fun getFragment(position: Int) = fragments.get(position)

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> VideoUpdateFragment.newInstance()
                1 -> HomeFragment.newInstance()
                2 -> PlaylistFragment.newInstance()
                3 -> OrganizeChannelFragment.newInstance()
                else -> Fragment()
            }
        }


        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            fragments.put(position, `object` as Fragment)
            super.setPrimaryItem(container, position, `object`)
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
    }
}
