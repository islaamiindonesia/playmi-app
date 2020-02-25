package com.example.playmi.util.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.playmi.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * Created by Kemal Amru Ramadhan on 27/07/2019.
 */
fun CollapsingToolbarLayout.onlyShowTitleWhenCollapsed(appBarLayout: AppBarLayout, title: String) {
    this.let { collapsingToolbar ->
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = title
                    collapsingToolbar.setCollapsedTitleTextColor(
                        ContextCompat.getColor(collapsingToolbar.context, R.color.white)
                    )
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title =
                        " "//careful there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })
    }
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, backClickHandler: (() -> Unit)? = null) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    toolbar.setNavigationOnClickListener {
        backClickHandler?.run { this() } ?: finish()
    }
}