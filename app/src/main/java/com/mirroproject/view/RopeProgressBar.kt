package com.mirroproject.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.mirroproject.R

/**
 * Created by reeman on 2017/10/31.
 */
class RopeProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val mBubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mLinesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val m1Dip: Float
    private val m1Sp: Float
    private var mProgress: Int = 0
    var max: Int = 0
        set(max) {
            var max = max
            max = Math.max(0, max)

            if (max != this.max) {

                dynamicRequestLayout()
                field = max
                if (mProgress > max) {
                    mProgress = max
                }

                postInvalidate()
            }
        }

    private var mPrimaryColor: Int = 0
    private var mSecondaryColor: Int = 0

    private var mSlack: Float = 0.toFloat()
    private var mDynamicLayout: Boolean = false
    private var mFormatter: ProgressFormatter? = null

    private val mBounds = Rect()
    private val mBubble = Path()
    private val mTriangle = Path()
    private var mAnimator: ValueAnimator? = null
    private var mBounceX: Float = 0.toFloat()
    private var mStartProgress: Int = 0
    private var mDeferred: Boolean = false
    private var mSlackSetByUser: Boolean = false

    private val mRequestLayoutRunnable = Runnable { requestLayout() }

    private val currentSlackHeight: Float
        get() {
            val max = max.toFloat()
            val offset: Float = if (max == 0f) 0f else progress / max
            return slack * perp(offset)
        }

    private val bubbleVerticalDisplacement: Float
        get() = bubbleMargin + bubbleHeight + triangleHeight

    val bubbleMargin: Float
        get() = dips(4f)

    /* padding */ val bubbleWidth: Float
        get() = mBounds.width() + dips(16f)

    /* padding */ val bubbleHeight: Float
        get() = mBounds.height() + dips(16f)

    val triangleWidth: Float
        get() = dips(12f)

    val triangleHeight: Float
        get() = dips(6f)

    val bubbleText: String
        get() {
            if (mFormatter != null) {
                return mFormatter!!.getFormattedText(progress, max)

            } else {
                val progress = (100 * progress / max.toFloat()).toInt()
                return progress.toString() + "%"
            }
        }

    var progress: Int
        get() = mProgress
        @Synchronized set(progress) {
            var progress = progress
            progress = clamp(progress.toFloat(), 0f, max.toFloat()).toInt()
            if (progress == mProgress) {
                return
            }

            if (!mDeferred) {
                bounceAnimation(progress)
            }

            dynamicRequestLayout()
            mProgress = progress
            postInvalidate()
        }

    var primaryColor: Int
        get() = mPrimaryColor
        set(color) {
            mPrimaryColor = color

            invalidate()
        }

    var secondaryColor: Int
        get() = mSecondaryColor
        set(color) {
            mSecondaryColor = color

            invalidate()
        }

    var slack: Float
        get() = mSlack
        set(slack) {
            mSlack = slack

            requestLayout()
            invalidate()
        }

    var strokeWidth: Float
        get() = mLinesPaint.strokeWidth
        set(width) {
            mLinesPaint.strokeWidth = width

            requestLayout()
            invalidate()
        }

    /**
     * Return a copy so that fields can only be modified through
     * [.setTextPaint]
     */
    var textPaint: Paint
        get() = Paint(mTextPaint)
        set(paint) {
            mTextPaint.set(paint)

            requestLayout()
            invalidate()
        }

    init {

        m1Dip = resources.displayMetrics.density
        m1Sp = resources.displayMetrics.scaledDensity

        var max = 0
        var progress = 0

        var width = dips(15f)
        var slack = dips(32f)
        var dynamicLayout = false

        var primaryColor = -0xf54e01
        var secondaryColor = -0x252526

        val a = context.obtainStyledAttributes(attrs, R.styleable.RopeProgressBar, defStyleAttr, 0)

        if (a != null) {
            max = a.getInt(R.styleable.RopeProgressBar_ropeMax, max)
            progress = a.getInt(R.styleable.RopeProgressBar_ropeProgress, progress)

            primaryColor = a.getColor(R.styleable.RopeProgressBar_ropePrimaryColor, primaryColor)
            secondaryColor = a.getColor(R.styleable.RopeProgressBar_ropeSecondaryColor, secondaryColor)
            slack = a.getDimension(R.styleable.RopeProgressBar_ropeSlack, slack)
            width = a.getDimension(R.styleable.RopeProgressBar_ropeStrokeWidth, width)
            dynamicLayout = a.getBoolean(R.styleable.RopeProgressBar_ropeDynamicLayout, false)

            mSlackSetByUser = a.hasValue(R.styleable.RopeProgressBar_ropeSlack)
            a.recycle()
        }

        mPrimaryColor = primaryColor
        mSecondaryColor = secondaryColor
        mSlack = slack
        mDynamicLayout = dynamicLayout

        mLinesPaint.strokeWidth = width
        mLinesPaint.style = Paint.Style.STROKE
        mLinesPaint.strokeCap = Paint.Cap.ROUND

        mBubblePaint.color = Color.WHITE
        mBubblePaint.style = Paint.Style.FILL
        mBubblePaint.pathEffect = CornerPathEffect(dips(2f))

        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = sp(18)
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = Typeface.create("sans-serif-condensed-light", Typeface.NORMAL)

        max = max
        progress = progress
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun dynamicRequestLayout() {
        if (mDynamicLayout) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mRequestLayoutRunnable.run()
            } else {
                post(mRequestLayoutRunnable)
            }
        }
    }

    @Synchronized override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (!mSlackSetByUser) {
            mSlack = View.MeasureSpec.getSize(widthMeasureSpec) * 0.1f
        }
        val progress = bubbleText
        mTextPaint.getTextBounds(progress, 0, progress.length, mBounds)

        val bubbleHeight = Math.ceil(bubbleVerticalDisplacement.toDouble()).toInt()
        val slack = if (mDynamicLayout) currentSlackHeight else slack

        val strokeWidth = strokeWidth
        val dh = Math.ceil((paddingTop.toFloat() + paddingBottom.toFloat() + strokeWidth + slack).toDouble()).toInt()

        setMeasuredDimension(View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                View.resolveSizeAndState(dh + bubbleHeight, heightMeasureSpec, 0))

        mTriangle.reset()
        mTriangle.moveTo(0f, 0f)
        mTriangle.lineTo(triangleWidth, 0f)
        mTriangle.lineTo(triangleWidth / 2f, triangleHeight)
        mTriangle.lineTo(0f, 0f)
    }

    @Synchronized override fun onDraw(canvas: Canvas) {

        val radius = strokeWidth / 2

        val bubbleDisplacement = bubbleVerticalDisplacement
        val top = paddingTop.toFloat() + radius + bubbleDisplacement
        val left = paddingLeft + radius
        val end = width.toFloat() - paddingRight.toFloat() - radius

        val max = max.toFloat()
        val offset: Float

        if (max == 0f) {
            offset = 0f
        } else {
            offset = progress / max
        }
        // final float slackHeight = getCurrentSlackHeight();//这个是会往下移动的
        val slackHeight = 0f// 目前设置为不动,效果为一个横线移动
        val progressEnd = clamp(lerp(left, end, offset) + mBounceX * perp(offset), left, end)

        mLinesPaint.color = mSecondaryColor
        canvas.drawLine(progressEnd, top + slackHeight, end, top, mLinesPaint)

        mLinesPaint.color = mPrimaryColor
        if (progressEnd == left) {
            mLinesPaint.style = Paint.Style.FILL
            canvas.drawCircle(left, top, radius, mLinesPaint)
            mLinesPaint.style = Paint.Style.STROKE
        } else {
            canvas.drawLine(left, top, progressEnd, top + slackHeight, mLinesPaint)
        }

        val progress = bubbleText
        mTextPaint.getTextBounds(progress, 0, progress.length, mBounds)

        // Draw the bubble text background
        val bubbleWidth = bubbleWidth
        val bubbleHeight = bubbleHeight
        mBubble.reset()
        mBubble.addRect(0f, 0f, bubbleWidth, bubbleHeight, Path.Direction.CW)

        val bubbleTop = Math.max(slackHeight, 0f)
        val bubbleLeft = clamp(progressEnd - bubbleWidth / 2, 0f, width - bubbleWidth)

        val saveCount = canvas.save()
        canvas.translate(bubbleLeft, bubbleTop)

        canvas.drawPath(mBubble, mBubblePaint)

        // Draw the triangle part of the bubble
        val triangleLeft = clamp(progressEnd - triangleWidth / 2 - bubbleLeft, 0f,
                width - triangleWidth)

        mTriangle.offset(triangleLeft, bubbleHeight)
        canvas.drawPath(mTriangle, mBubblePaint)
        mTriangle.offset(-triangleLeft, -bubbleHeight)

        // Draw the progress text part of the bubble
        val textX = bubbleWidth / 2
        val textY = bubbleHeight - dips(8f)

        canvas.drawText(progress, textX, textY, mTextPaint)

        canvas.restoreToCount(saveCount)
    }

    fun defer() {
        if (!mDeferred) {
            mDeferred = true
            mStartProgress = progress
        }
    }

    fun endDefer() {
        if (mDeferred) {
            mDeferred = false
            bounceAnimation(mStartProgress)
        }
    }


    private fun bounceAnimation(startProgress: Int) {
        // Moving the progress by at least 1/4 of the total distance will invoke
        // the "max" possible slack bouncing at the end progress value
        val diff = Math.abs(startProgress - mProgress)
        val diffPercent = Math.min(1f, 4 * diff / max.toFloat())
        if (mAnimator != null) {
            mAnimator!!.cancel()
        }

        mAnimator = ValueAnimator.ofFloat(0f, diffPercent * triangleWidth)
        mAnimator!!.interpolator = INTERPOLATOR
        mAnimator!!.duration = 1000L
        mAnimator!!.addUpdateListener { animation ->
            mBounceX = animation.animatedValue as Float
            invalidate()
        }
        mAnimator!!.start()
    }

    fun setDynamicLayout(isDynamic: Boolean) {
        if (mDynamicLayout != isDynamic) {
            mDynamicLayout = isDynamic

            requestLayout()
            invalidate()
        }
    }

    fun setTypeface(typeface: Typeface) {
        mTextPaint.typeface = typeface

        requestLayout()
        invalidate()
    }

    fun setProgressFormatter(formatter: ProgressFormatter) {
        mFormatter = formatter

        requestLayout()
        invalidate()
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, value))
    }

    private fun perp(t: Float): Float {
        // eh, could be more mathematically accurate to use a catenary function,
        // but the max difference between the two is only 0.005
        return (-Math.pow((2 * t - 1).toDouble(), 2.0) + 1).toFloat()
    }

    private fun lerp(v0: Float, v1: Float, t: Float): Float {
        return if (t == 1f) v1 else v0 + t * (v1 - v0)
    }

    private fun dips(dips: Float): Float {
        return dips * m1Dip
    }

    private fun sp(sp: Int): Float {
        return sp * m1Sp
    }

    companion object {

        private val INTERPOLATOR = DampingInterpolator(5f)
    }

}
