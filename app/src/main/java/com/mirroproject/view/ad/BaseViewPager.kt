package com.mirroproject.view.ad

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by reeman on 2017/10/31.
 */
class BaseViewPager : ViewPager {
    private var scrollable = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    fun setScrollable(enable: Boolean) {
        scrollable = enable
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (scrollable) {
            super.onInterceptTouchEvent(event)
        } else {
            false
        }
    }
}