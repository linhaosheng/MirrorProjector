package com.mirroproject.view.ad

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mirroproject.R
import com.mirroproject.entity.ADInfo
import com.mirroproject.entity.EventType
import com.mirroproject.view.CycleViewPagerHandler
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by reeman on 2017/11/3.
 */
/**
 * 实现可循环，可轮播的viewpager
 */
@SuppressLint("NewApi")
class CycleViewPager : Fragment(), ViewPager.OnPageChangeListener {

    private val imageViews = ArrayList<ImageView>()
    lateinit var indicators: Array<ImageView>
    private var viewPagerFragmentLayout: FrameLayout? = null
    lateinit var indicatorLayout: LinearLayout // 指示器
    lateinit var viewPager1: BaseViewPager
    private val parentViewPager: BaseViewPager? = null
    private var adapter: ViewPagerAdapter? = null
    private var time = 5000 // 默认轮播时间
    private val currentPosition = 0 // 轮播当前位置
    private val isScrolling = false // 滚动框是否滚动着
    private var isCycle = false // 是否循环
    private var isWheel = false // 是否轮播
    private var releaseTime: Long = 0 // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换
    private val WHEEL = 100 // 转动
    private val WHEEL_WAIT = 101 // 等待
    private var mImageCycleViewListener: ImageCycleViewListener? = null
    private var infos: List<ADInfo.Pos1Bean>? = null
    private var isSendEvent = true
    lateinit var handler: Handler

    companion object {
        var position: Int = 0
        var wheelTime: Int = 0
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(activity).inflate(
                R.layout.view_cycle_viewpager_contet, null)
        viewPager1 = view.findViewById<View>(R.id.viewPager) as BaseViewPager
        viewPager1.setFocusable(false)
        indicatorLayout = view
                .findViewById<View>(R.id.layout_viewpager_indicator) as LinearLayout
        indicatorLayout.setFocusable(false)
        viewPagerFragmentLayout = view
                .findViewById<View>(R.id.layout_viewager_content) as FrameLayout

        handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg!!.what == WHEEL && imageViews.size != 0) {
                    if (!isScrolling) {
                        val max = imageViews.size
                        val current = viewPager1.getCurrentItem()
                        if (current + 1 == max) {
                            viewPager1.setCurrentItem(0, true)
                        } else {
                            viewPager1.setCurrentItem(current + 1, true)
                        }
                    }

                    releaseTime = System.currentTimeMillis()
                    handler!!.removeCallbacks(runnable)
                    val time = Integer.parseInt(infos!!.get(position).timelength)
                    if (infos!!.size > position && time > 0) {
                        wheelTime = time * 1000
                    } else {
                        wheelTime = time
                    }
                    if (isSendEvent) {
                        EventBus.getDefault().post(EventType.CycleEventType(1))
                    }
                    handler!!.postDelayed(runnable, wheelTime as Long)
                    return
                }
                if (msg.what == WHEEL_WAIT && imageViews.size != 0) {
                    handler!!.removeCallbacks(runnable)
                    val time = Integer.parseInt(infos!!.get(position).timelength)
                    if (infos!!.size > position && time > 0) {
                        wheelTime = time * 1000
                    } else {
                        wheelTime = time
                    }
                    if (isSendEvent) {
                        EventBus.getDefault().post(EventType.CycleEventType(1))
                    }
                    handler!!.postDelayed(runnable, wheelTime as Long)
                }
            }
        }
        return view
    }


    fun setData(views: List<ImageView>, list: List<ADInfo.Pos1Bean>, listener: ImageCycleViewListener) {
        setData(views, list, listener, 0)
    }

    fun setIsSendEvent(isSendEvent: Boolean) {
        this.isSendEvent = isSendEvent
    }

    /**
     * 初始化viewpager
     *
     * @param views        要显示的views
     * @param showPosition 默认显示位置
     */
    fun setData(views: List<ImageView>, list: List<ADInfo.Pos1Bean>, listener: ImageCycleViewListener, showPosition: Int) {
        var showPosition = showPosition
        mImageCycleViewListener = listener
        infos = list
        this.imageViews.clear()

        if (views.size == 0) {
            viewPagerFragmentLayout!!.visibility = View.GONE
            return
        }

        for (item in views) {
            this.imageViews.add(item)
        }

        var ivSize = views.size

        // 设置指示器
        indicators = Array(ivSize, { index -> indicators[index] })
        indicatorLayout!!.removeAllViews()
        for (i in indicators!!.indices) {
            val view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.view_cycle_viewpager_indicator, null)
            indicators[i] = view.findViewById<View>(R.id.image_indicator) as ImageView
            indicatorLayout!!.addView(view)
        }

        adapter = ViewPagerAdapter()

        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(0)

        viewPager1!!.setOffscreenPageLimit(views.size)
        viewPager1!!.setOnPageChangeListener(this)
        viewPager1!!.setAdapter(adapter)
        if (showPosition < 0 || showPosition >= views.size)
            showPosition = 0
        if (isCycle) {
            showPosition = showPosition + 1
        }
        viewPager1!!.setCurrentItem(showPosition)
    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    fun setIndicatorCenter() {
        val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        indicatorLayout!!.layoutParams = params
    }

    /**
     * 是否循环，默认不开启，开启前，请将views的最前面与最后面各加入一个视图，用于循环
     *
     * @param isCycle 是否循环
     */
    fun setCycle(isCycle: Boolean) {
        this.isCycle = isCycle
    }

    /**
     * 是否处于循环状态
     *
     * @return
     */
    fun isCycle(): Boolean {
        return isCycle
    }

    /**
     * 设置是否轮播，默认不轮播,轮播一定是循环的
     *
     * @param isWheel
     */
    fun setWheel(isWheel: Boolean) {
        this.isWheel = isWheel
        isCycle = true
        if (isWheel) {
            handler!!.postDelayed(runnable, time as Long)
        }
    }

    /**
     * 是否处于轮播状态
     *
     * @return
     */
    fun isWheel(): Boolean {
        return isWheel
    }

    internal val runnable: Runnable = Runnable {
        if (getActivity() != null && !getActivity().isFinishing()
                && isWheel) {
            val now = System.currentTimeMillis()
            // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
            if (now - releaseTime > time - 500) {
                handler!!.sendEmptyMessage(WHEEL)
            } else {
                handler!!.sendEmptyMessage(WHEEL_WAIT)
            }
        }
    }

    /**
     * 释放指示器高度，可能由于之前指示器被限制了高度，此处释放
     */
    fun releaseHeight() {
        getView()!!.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT
        refreshData()
    }

    /**
     * 设置轮播暂停时间，即没多少秒切换到下一张视图.默认5000ms
     *
     * @param time 毫秒为单位
     */
    fun setTime(time: Int) {
        this.time = time
    }

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
    fun refreshData() {
        if (adapter != null)
            adapter!!.notifyDataSetChanged()
    }

    /**
     * 隐藏CycleViewPager
     */
    fun hide() {
        viewPagerFragmentLayout!!.visibility = View.GONE
    }

    /**
     * 返回内置的viewpager
     *
     * @return viewPager
     */
    fun getViewPager(): BaseViewPager? {
        return viewPager1
    }

    /**
     * 页面适配器 返回对应的view
     *
     * @author Yuedong Li
     */
    private inner class ViewPagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return imageViews.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val v = imageViews[position]
            if (mImageCycleViewListener != null) {
                v.setOnClickListener { v -> mImageCycleViewListener!!.onImageClick(infos!![viewPager1!!.getCurrentItem()], viewPager1!!.getCurrentItem(), v) }
            }
            container.addView(v)
            return v
        }

        override fun getItemPosition(`object`: Any?): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    override fun onPageScrollStateChanged(arg0: Int) {
        /*  if (arg0 == 1) { // viewPager在滚动
            isScrolling = true;
            return;
        } else if (arg0 == 0) { // viewPager滚动结束
            if (parentViewPager != null)
                parentViewPager.setScrollable(true);

            releaseTime = System.currentTimeMillis();

            viewPager.setCurrentItem(currentPosition, false);

        }
        isScrolling = false;*/
    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

    override fun onPageSelected(arg0: Int) {
        /*   int max = imageViews.size() - 1;
        int position = arg0;
        currentPosition = arg0;
        if (isCycle) {
            if (arg0 == 0) {
                currentPosition = max - 1;
            } else if (arg0 == max) {
                currentPosition = 1;
            }
            position = currentPosition - 1;
        }
        */
        setIndicator(arg0)
    }

    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    fun setScrollable(enable: Boolean) {
        viewPager1!!.setScrollable(enable)
    }

    /**
     * 返回当前位置,循环时需要注意返回的position包含之前在views最前方与最后方加入的视图，即当前页面试图在views集合的位置
     *
     * @return
     */
    fun getCurrentPostion(): Int {
        return viewPager1!!.getCurrentItem()
    }

    /**
     * 设置指示器
     *
     * @param selectedPosition 默认指示器位置
     */
    private fun setIndicator(selectedPosition: Int) {
        for (i in indicators!!.indices) {
            indicators!![i]
                    .setBackgroundResource(R.drawable.icon_point)
        }
        if (indicators!!.size > selectedPosition)
            indicators!![selectedPosition]
                    .setBackgroundResource(R.drawable.icon_point_pre)
    }

    /**
     * 如果当前页面嵌套在另一个viewPager中，为了在进行滚动时阻断父ViewPager滚动，可以 阻止父ViewPager滑动事件
     * 父ViewPager需要实现ParentViewPager中的setScrollable方法
     */
    fun disableParentViewPagerTouchEvent(parentViewPager: BaseViewPager?) {
        if (parentViewPager != null)
            parentViewPager!!.setScrollable(false)
    }


    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    interface ImageCycleViewListener {

        /**
         * 单击图片事件
         *
         * @param imageView
         */
        fun onImageClick(info: ADInfo.Pos1Bean, postion: Int, imageView: View)
    }
}