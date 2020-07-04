package id.islaami.playmi.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.data.model.category.Category
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.ui.setting.SettingActivity
import id.islaami.playmi.ui.video.VideoCategoryFragment
import id.islaami.playmi.util.ERROR_CONNECTION
import id.islaami.playmi.util.ERROR_CONNECTION_TIMEOUT
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.createMaterialAlertDialog
import id.islaami.playmi.util.ui.showLongToast
import id.islaami.playmi.util.ui.showMaterialAlertDialog
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.home_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment(var list: List<Category> = emptyList()) : BaseFragment() {
    private val viewModel: HomeViewModel by viewModel()

    var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.home_fragment, container, false)

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

        viewModel.initHome()
        observeGetAllCategory()
    }

    fun refresh() {
        viewModel.getAllCategory()
    }

    private fun observeGetAllCategory() {
        val dialog = context?.createMaterialAlertDialog(
            "Coba Lagi",
            positiveCallback = { refresh() },
            dismissCallback = { refresh() }
        )

        viewModel.getCategoryListResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    dialog?.dismiss()
                }
                SUCCESS -> {
                    if (!result.data.isNullOrEmpty()) {
                        viewPagerAdapter = ViewPagerAdapter()
                        viewPager.adapter = viewPagerAdapter
                        viewPager.offscreenPageLimit = 4
                        tabLayout.setupWithViewPager(viewPager)

                        setupTab(result.data)
                    }
                }
                ERROR -> {
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            dialog?.let {
                                context?.showMaterialAlertDialog(
                                    it,
                                    getString(R.string.error_connection)
                                )
                            }
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            dialog?.let {
                                context?.showMaterialAlertDialog(
                                    it,
                                    getString(R.string.error_connection_timeout)
                                )
                            }
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) {
                                showLongToast(
                                    context,
                                    it
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setupTab(categories: List<Category>) {
        list = categories
        viewPagerAdapter?.notifyDataSetChanged()
    }

    inner class ViewPagerAdapter :
        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return VideoCategoryFragment.newInstance(list[position].ID.value())
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return list[position].name.toString().toUpperCase(Locale("id", "ID"))
        }
    }

    companion object {
        fun newInstance(): Fragment = HomeFragment()
    }
}
