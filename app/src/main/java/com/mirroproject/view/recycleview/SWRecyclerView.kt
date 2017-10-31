package com.mirroproject.view.recycleview

import android.content.Context
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.reflect.InvocationTargetException

/**
 * Created by reeman on 2017/10/31.
 */
class SWRecyclerView: RecyclerView {

    private val TAG = SWRecyclerView::class.java.simpleName
    private var mSelectedItemCentered = true
    private var mSelectedItemOffsetStart: Int = 0
    private var mSelectedItemOffsetEnd: Int = 0
    private var offset = -1
    private var mDuration = 0


    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}


    override fun requestChildFocus(child: View?, focused: View) {
        // 获取焦点框居中的位置
        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = if (!isVertical()) getFreeWidth() - child.width else getFreeHeight() - child.height
                mSelectedItemOffsetStart /= 2
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart
            }
        }
        super.requestChildFocus(child, focused)
    }

    override fun requestChildRectangleOnScreen(child: View, rect: Rect, immediate: Boolean): Boolean {
        val parentLeft = getPaddingLeft()
        val parentTop = getPaddingTop()
        val parentRight = getWidth() - getPaddingRight()
        val parentBottom = getHeight() - getPaddingBottom()

        val childLeft = child.left + rect.left
        val childTop = child.top + rect.top
        val childRight = childLeft + rect.width()
        val childBottom = childTop + rect.height()

        val offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart)
        val offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart)
        val offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd)
        val offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd)

        val canScrollHorizontal = getLayoutManager().canScrollHorizontally()
        val canScrollVertical = getLayoutManager().canScrollVertically()

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        val dx: Int
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = if (offScreenRight != 0)
                    offScreenRight
                else
                    Math.max(offScreenLeft, childRight - parentRight)
            } else {
                dx = if (offScreenLeft != 0)
                    offScreenLeft
                else
                    Math.min(childLeft - parentLeft, offScreenRight)
            }
        } else {
            dx = 0
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        val dy: Int
        if (canScrollVertical) {
            dy = if (offScreenTop != 0) offScreenTop else Math.min(childTop - parentTop, offScreenBottom)
        } else {
            dy = 0
        }

        offset = if (isVertical()) dy else dx

        if (dx != 0 || dy != 0) {
            if (mDuration == 0)
                smoothScrollBy(dx, dy)
            else
                smoothScrollBy(dx, dy, mDuration)
            Log.i(TAG, "requestChildRectangleOnScreen: immediate--->" + immediate)
            return true
        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate()

        return false
    }

    private fun getFreeWidth(): Int {
        return getWidth() - getPaddingLeft() - getPaddingRight()
    }

    private fun getFreeHeight(): Int {
        return getHeight() - getPaddingTop() - getPaddingBottom()
    }

    /**
     * 设置默认选中.
     */
    fun setDefaultSelect(pos: Int) {
        val vh = findViewHolderForAdapterPosition(pos) as RecyclerView.ViewHolder
        requestFocusFromTouch()
        vh?.itemView?.requestFocus()
    }

    /**
     * 设置SWRecyclerView的滑动速度
     *
     * @param duration
     */
    fun setDuration(duration: Int) {
        mDuration = duration
    }

    /**
     * 设置选中的Item居中；
     *
     * @param isCentered
     */
    fun setSelectedItemAtCentered(isCentered: Boolean) {
        this.mSelectedItemCentered = isCentered
    }

    /**
     * 判断是垂直，还是横向.
     */
    private fun isVertical(): Boolean {
        val layout = getLayoutManager() as LinearLayoutManager
        return layout.orientation == LinearLayoutManager.VERTICAL
    }

    /**
     * 利用反射拿到RecyclerView中的mViewFlinger属性，
     * 再调用其smoothScrollBy(int dx, int dy, int duration) 方法实现RecyclerViewTV速度的控制
     *
     * @param dx
     * @param dy
     * @param duration
     */
    fun smoothScrollBy(dx: Int, dy: Int, duration: Int) {
        var dx = dx
        var dy = dy
        try {
            var c: Class<*>? = null
            try {
                c = Class.forName("android.support.v7.widget.RecyclerView")//获得Class对象
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                return
            }

            val mLayoutField = c!!.getDeclaredField("mLayout")     //根据属性名称，获得类的属性成员Field
            mLayoutField.isAccessible = true                       //设置为可访问状态
            var mLayout: RecyclerView.LayoutManager? = null
            try {
                mLayout = mLayoutField.get(this) as RecyclerView.LayoutManager   //获得该属性对应的对象
                if (mLayout == null) {
                    return
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                return
            }

            val mLayoutFrozen = c.getDeclaredField("mLayoutFrozen")
            mLayoutFrozen.isAccessible = true
            try {
                if (mLayoutFrozen.get(this) as Boolean) {
                    return
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                return
            }

            if (!mLayout.canScrollHorizontally()) {
                dx = 0
            }
            if (!mLayout.canScrollVertically()) {
                dy = 0
            }
            val mViewFlingerField = c.getDeclaredField("mViewFlinger")
            mViewFlingerField.isAccessible = true
            try {
                var ViewFlingerClass: Class<*>? = null
                try {
                    //由于内部类是私有的，所以不能直接得到内部类名，
                    //通过mViewFlingerField.getType().getName()
                    //可以得到私有内部类的完整类名
                    ViewFlingerClass = Class.forName(mViewFlingerField.type.name)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    return
                }

                //根据方法名，获得我们的目标方法对象。第一个参数是方法名，后面的是该方法的入参类型。
                // 注意Integer.class与int.class的不同。
                val smoothScrollBy = ViewFlingerClass!!.getDeclaredMethod("smoothScrollBy",
                        Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                smoothScrollBy.isAccessible = true//设置为可操作状态
                if (dx != 0 || dy != 0) {
                    Log.d("MySmoothScrollBy", "dx=$dx dy=$dy")
                    try {
                        //唤醒（调用）方法，
                        // mViewFlingerField.get(this)指明是哪个对象调用smoothScrollBy。
                        // dx, dy, duration 是smoothScrollBy所需参数
                        smoothScrollBy.invoke(mViewFlingerField.get(this), dx, dy, duration)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }

                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                return
            }

        } catch (e: NoSuchFieldException) {
            return
        }

    }

}