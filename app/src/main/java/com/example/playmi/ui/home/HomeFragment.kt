package com.example.playmi.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.example.playmi.R
import com.example.playmi.data.model.category.Category
import com.example.playmi.ui.base.BaseFragment
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.handleApiError
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeGetAllCategory()
    }

    private fun observeGetAllCategory() {
        viewModel.getCategoryListResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    if (!result.data.isNullOrEmpty()) {
                        setupTab(result.data)
                    }
                }
                ERROR -> {
                    handleApiError(errorMessage = result.message) { message ->
                        showSnackbar(message)
                    }
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
            return VideoCategoryFragment.newInstance(list[position].name.toString())
        }

        override fun getCount(): Int {
            return numberOfTabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return list[position].name.toString()
        }
    }

    companion object {
        fun newInstance(): Fragment = HomeFragment()
    }
}
