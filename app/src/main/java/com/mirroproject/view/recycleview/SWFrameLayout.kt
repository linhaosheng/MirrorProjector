package com.mirroproject.view.recycleview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.mirroproject.R

/**
 * Created by reeman on 2017/10/31.
 */
class SWFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), View.OnFocusChangeListener, View.OnClickListener {
    private val mFocus = true

    init {
        init()
    }

    protected fun init() {
        setOnClickListener(this)
        onFocusChangeListener = this

        setBackgroundResource(R.drawable.selector_home_list)
        isFocusable = mFocus
        isFocusableInTouchMode = mFocus

    }

    override fun onClick(view: View) {

    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        Log.i(TAG, "onFocusChanged: gainFocus = $gainFocus, direction = $direction")
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (null == v) {
            Log.i(TAG, "onFocusChange: v == null")
            return
        }
        Log.i(TAG, "onFocusChange: hasFocus = " + hasFocus)
        if (hasFocus) {
            v.bringToFront()
            v.animate().scaleX(FOCUS_SCALE).scaleY(FOCUS_SCALE).setDuration(ANIMATION_DURATION.toLong()).start()
            v.isSelected = true
        } else {
            v.animate().scaleX(NORMAL_SCALE).scaleY(NORMAL_SCALE).setDuration(ANIMATION_DURATION.toLong()).start()
            v.isSelected = false
        }
    }

    companion object {

        private val TAG = SWFrameLayout::class.java.simpleName
        protected val ANIMATION_DURATION = 300
        protected val FOCUS_SCALE = 1.1f
        protected val NORMAL_SCALE = 1.0f
    }
}