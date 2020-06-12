package id.islaami.playmi.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.data.model.category.Category
import id.islaami.playmi.ui.base.BaseFragment
import id.islaami.playmi.ui.setting.SettingActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.showSnackbar
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.home_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.initHome()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
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

        observeGetAllCategory()
    }

    private fun observeGetAllCategory() {
        viewModel.getCategoryListResultLd.observe(viewLifecycleOwner, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    if (!result.data.isNullOrEmpty()) {
                        setupTab(result.data)
                    }
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message)
                    { message -> showSnackbar(message) }
                }
            }
        })
    }

    private fun setupTab(list: List<Category>) {
        val categories = ArrayList<Category>()
        categories.add(0, Category(0, "Semua", 0, ""))
        categories.addAll(list)

        categories.forEach { category ->
            tabLayout.addTab(tabLayout.newTab().setText(category.name))
        }

        viewPager.adapter = ViewPagerAdapter(categories, tabLayout.tabCount)
        viewPager.offscreenPageLimit = categories.size
        tabLayout.setupWithViewPager(viewPager)
    }

    inner class ViewPagerAdapter(var list: List<Category>, var numberOfTabs: Int = 0) :
        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return VideoCategoryFragment.newInstance(list[position].ID.value())
        }

        override fun getCount(): Int {
            return numberOfTabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return list[position].name.toString().toUpperCase(Locale("id", "ID"))
        }
    }

    companion object {
        fun newInstance(): Fragment = HomeFragment()
    }
}
