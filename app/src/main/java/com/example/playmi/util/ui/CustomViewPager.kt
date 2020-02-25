package com.example.playmi.util.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Kemal Amru Ramadhan on 26/06/2019.
 */
class CustomViewPager : ViewPager {
    private var disable: Boolean = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (disable) false else super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (disable) false else super.onTouchEvent(event)
    }

    fun disableScroll(disable: Boolean) {
        //When disable = true not work the scroll and when disble = false work the scroll
        this.disable = disable
    }
}