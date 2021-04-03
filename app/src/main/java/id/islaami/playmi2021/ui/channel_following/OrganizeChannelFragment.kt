package id.islaami.playmi2021.ui.channel_following

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseFragment
import id.islaami.playmi2021.ui.base.BaseRecyclerViewFragment
import id.islaami.playmi2021.ui.setting.SettingActivity
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.organize_channel_fragment.*
import kotlinx.android.synthetic.main.organize_channel_fragment.toolbar

class OrganizeChannelFragment : BaseFragment(), BaseRecyclerViewFragment {

    var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.organize_channel_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.menu_main)

        // Get the SearchView and set the searchable configuration
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (toolbar.menu.findItem(R.id.mainSearch).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = "Cari Video"
            isIconified = true // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mainSetting -> {
                    SettingActivity.startActivity(context)

                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }

        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)

        val tabLabelList = arrayOf(
            getString(R.string.following),
            getString(R.string.hidden)
        )

        val tabColorDefault = context?.let { ContextCompat.getColor(it, R.color.main_tab) }
        val tabColorSelected = context?.let { ContextCompat.getColor(it, R.color.accent) }

        for (tabPosition in 0 until (viewPagerAdapter?.count ?: 0)) {
            val tabItem: TextView =
                LayoutInflater.from(context)
                    .inflate(R.layout.organize_channel_tab_item, null) as TextView

            tabItem.text = tabLabelList[tabPosition]
            tabItem.gravity = Gravity.CENTER
            if (tabPosition == 0) {
                tabItem.setTextColor(tabColorSelected.value())
            }
            tabLayout.getTabAt(tabPosition)?.customView = tabItem
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPagerAdapter?.getFragment(tab?.position ?: 0)?.scrollToTop()
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

        private val fragments = SparseArray<BaseRecyclerViewFragment>()

        fun getFragment(position: Int) = fragments.get(position)

        override fun getItem(position: Int): Fragment {
            return if (position == 0) ChannelFollowingFragment.newInstance() else ChannelHiddenFragment.newInstance()
        }

        override fun getCount(): Int {
            return 2
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            fragments.put(position, `object` as BaseRecyclerViewFragment)
            super.setPrimaryItem(container, position, `object`)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.following) else getString(R.string.hidden)
        }
    }

    companion object {
        fun newInstance(): Fragment = OrganizeChannelFragment()
    }

    override fun scrollToTop() {
        viewPagerAdapter?.getFragment(tabLayout.selectedTabPosition)?.scrollToTop()
    }
}
