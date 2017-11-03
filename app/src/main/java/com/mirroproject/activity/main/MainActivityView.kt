package com.mirroproject.activity.main

import android.widget.GridView
import android.widget.RelativeLayout
import com.mirroproject.view.MarqueeView
import com.mirroproject.view.ad.CycleViewPager

/**
 * Created by reeman on 2017/11/3.
 */
interface MainActivityView {

    fun cycleViewPager(): CycleViewPager

    fun marqueeView(): MarqueeView

    fun gridView(): GridView

    fun _rela_db_hair(): RelativeLayout
}
