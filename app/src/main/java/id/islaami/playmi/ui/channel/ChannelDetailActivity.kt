package id.islaami.playmi.ui.channel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import id.islaami.playmi.R
import id.islaami.playmi.data.model.channel.Channel
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.*
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.channel_detail_activity.*
import kotlinx.android.synthetic.main.channel_detail_activity.successLayout
import kotlinx.android.synthetic.main.channel_detail_activity.swipeRefreshLayout
import kotlinx.android.synthetic.main.channel_detail_activity.toolbar
import kotlinx.android.synthetic.main.video_category_fragment.*
import kotlinx.android.synthetic.main.video_category_fragment.recyclerView
import kotlinx.android.synthetic.main.watch_later_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChannelDetailActivity : BaseActivity() {
    val viewModel: ChannelViewModel by viewModel()

    var name: String? = null
    var channelID = 0

    var channel: Channel? = null
    var notif: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_detail_activity)

        setupToolbar(toolbar)

        name = intent.getStringExtra(CHANNEL_NAME)
        channelID = intent.getIntExtra(CHANNEL_ID, 0)

        viewModel.initChannelDetail(channelID)
        observeChannelDetail()
        observeFollowResult()
        observeUnfollowResult()
        observeShowResult()
        observeHideResult()

        setupViewPager()

        setupButton()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { refresh() }
        }
    }

    private fun setupButton() {
        btnFollow.setOnClickListener {
            PlaymiDialogFragment.show(
                supportFragmentManager,
                text = getString(R.string.channel_follow, name),
                okCallback = { viewModel.followChannel(channelID) }
            )
        }

        btnUnfollow.setOnClickListener {
            PlaymiDialogFragment.show(
                supportFragmentManager,
                text = getString(R.string.channel_unfollow, name),
                okCallback = { viewModel.unfollowChannel(channelID) }
            )
        }

        btnHide.setOnClickListener {
            PlaymiDialogFragment.show(
                supportFragmentManager,
                text = getString(R.string.channel_hide, name),
                okCallback = { viewModel.hideChannel(channelID) }
            )
        }

        btnShow.setOnClickListener {
            viewModel.showChannel(channelID)
        }
    }

    private fun setupViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)

        val tabLabelList = arrayOf(
            getString(R.string.video),
            getString(R.string.about)
        )

        val tabColorDefault = ContextCompat.getColor(this, R.color.text_color)
        val tabColorSelected = ContextCompat.getColor(this, R.color.accent)

        for (tabPosition in 0 until viewPagerAdapter.count) {
            val tabItem: TextView =
                LayoutInflater.from(this)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_channel, menu)

        notif = menu.findItem(R.id.channelNotif)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.channelNotif -> {
                PopupMenu(this, findViewById(R.id.channelNotif), Gravity.END).apply {
                    inflate(R.menu.menu_popup_channel_notif)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.notifEnabled -> {
                                showLongToast("Enable Notif")
                                notif?.icon =
                                    ContextCompat.getDrawable(
                                        this@ChannelDetailActivity,
                                        R.drawable.ic_notification
                                    )

                                true
                            }
                            else -> {
                                showLongToast("Disable Notif")
                                notif?.icon =
                                    ContextCompat.getDrawable(
                                        this@ChannelDetailActivity,
                                        R.drawable.ic_notification_disable
                                    )

                                true
                            }
                        }
                    }

                    show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun Channel.showData() {
        notif?.isVisible = isFollowed == true

        (supportFragmentManager.fragments[1] as ChannelAboutFragment).apply {
            setDescription(bio.fromHtmlToSpanned())
        }

        channelImage.loadImage(thumbnail)
        channelName.text = name
        videoCount.text = videos.toString()
        followerCount.text = followers.toString()

        if (isFollowed == true) {
            btnUnfollow.setVisibilityToVisible()
            btnFollow.setVisibilityToGone()
        } else {
            btnFollow.setVisibilityToVisible()
            btnUnfollow.setVisibilityToGone()
        }

        if (isBlacklisted == true) {
            hiddenLayout.setVisibilityToVisible()
            showLayout.setVisibilityToGone()
        } else {
            showLayout.setVisibilityToVisible()
            hiddenLayout.setVisibilityToGone()
        }
    }

    private fun refresh() {
        viewModel.getChannelDetail(channelID)
        (supportFragmentManager.fragments[0] as ChannelVideoFragment).apply {
            refresh()
        }
    }

    companion object {
        private val CHANNEL_NAME = "CHANNEL_NAME"
        private val CHANNEL_ID = "CHANNEL_ID"

        fun startActivity(context: Context?, channelName: String, channelID: Int) {
            context?.startActivity(
                Intent(context, ChannelDetailActivity::class.java)
                    .putExtra(CHANNEL_NAME, channelName)
                    .putExtra(CHANNEL_ID, channelID)
            )
        }
    }

    private fun observeChannelDetail() {
        viewModel.channelDetailResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                    successLayout.setVisibilityToGone()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    successLayout.setVisibilityToVisible()

                    channel = result.data ?: Channel()
                    channel?.showData()
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            RefreshDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = getString(R.string.error_connection),
                                btnOk = "Coba Lagi",
                                okCallback = { refresh() },
                                outsideTouchCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            RefreshDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = getString(R.string.error_connection_timeout),
                                btnOk = "Coba Lagi",
                                okCallback = { refresh() },
                                outsideTouchCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) { message ->
                                showLongToast(message)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun observeShowResult() {
        viewModel.showChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Kanal ditampilkan")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }

    private fun observeHideResult() {
        viewModel.hideChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Kanal telah disembunyikan")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }

    private fun observeFollowResult() {
        viewModel.followChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhasil mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }

    private fun observeUnfollowResult() {
        viewModel.unfollowChannelResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Berhenti mengikuti")
                    refresh()
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) ChannelVideoFragment.newInstance(channelID)
            else ChannelAboutFragment.newInstance()
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.video) else getString(R.string.about)
        }
    }
}
