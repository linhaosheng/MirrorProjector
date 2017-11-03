package com.mirroproject.view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.mirroproject.R
import java.util.*

/**
 * Created by reeman on 2017/11/3.
 */
/***
 * 使用方法：
 * 1：
 * 2：
 * 3：
 */

class MarqueeView @JvmOverloads constructor(var mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SurfaceView(mContext, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var mTextSize = 100f //字体大小

    private var mTextColor = Color.RED //字体的颜色

    private var mIsRepeat: Boolean = false//是否重复滚动

    private var mStartPoint: Int = 0// 开始滚动的位置  0是从最左面开始    1是从最末尾开始

    private var mDirection: Int = 0//滚动方向 0 向左滚动   1向右滚动

    private var mSpeed: Int = 0//滚动速度

    private var holder1: SurfaceHolder? = null

    private var mTextPaint: TextPaint? = null

    private var mThread: MarqueeView.MarqueeViewThread? = null

    private var margueeString: String? = null

    private var textWidth = 0
    private var textHeight = 0

    private val ShadowColor = Color.BLACK

    var currentX = 0// 当前x的位置

    var sepX = 5//每一步滚动的距离
    private var TAG = ""

    var width1: Int = 0
    internal var canvas: Canvas? = null
    private var sufaceCreate: Boolean = false

    lateinit var fontMetrics: Paint.FontMetrics

    var isRun = false//是否在运行
    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                ROLL_OVER -> {
                    stopScroll()
                    if (mOnMargueeListener != null) {
                        mOnMargueeListener!!.onRollOver()
                    }
                }
            }
        }
    }

    internal var mOnMargueeListener: MarqueeView.OnMargueeListener? = null

    init {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0)
        mTextColor = a.getColor(R.styleable.MarqueeView_textcolor, Color.WHITE)
        mTextSize = a.getDimension(R.styleable.MarqueeView_textSize, 48f)
        mIsRepeat = a.getBoolean(R.styleable.MarqueeView_isRepeat, false)
        mStartPoint = a.getInt(R.styleable.MarqueeView_startPoint, 0)
        mDirection = a.getInt(R.styleable.MarqueeView_direction, 0)
        mSpeed = a.getInt(R.styleable.MarqueeView_speedView, 120)
        a.recycle()

        holder1 = this.getHolder()
        holder!!.addCallback(this)
        mTextPaint = TextPaint()
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.LEFT
        setZOrderOnTop(true)//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT)//使窗口支持透明度
        //        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //width = wm.getDefaultDisplay().getWidth();
        width1 = dip2px(mContext, 985f)
    }

    fun setText(msg: String) {
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg)
        }
    }

    protected fun measurementsText(msg: String) {
        margueeString = msg
        mTextPaint!!.textSize = mTextSize
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.color = mTextColor
        textWidth = mTextPaint!!.measureText(margueeString).toInt()
        fontMetrics = mTextPaint!!.fontMetrics
        textHeight = fontMetrics.bottom.toInt()
        if (mStartPoint == 0)
            currentX = 0
        else
            currentX = width - paddingLeft - paddingRight
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        this.holder1 = holder
        synchronized(this) {
            sufaceCreate = true
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mThread != null) {
            isRun = true
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        synchronized(this) {
            sufaceCreate = false
            if (mThread != null) {
                isRun = false
            }
        }
    }

    /**
     * 开始滚动
     */
    fun startScroll(TAG: String) {
        if (mThread == null && !isRun) {
            mThread = MarqueeViewThread(holder)//创建一个绘图线程
        } else if (mThread == null && isRun) {
            return
        }
        this.TAG = TAG
        isRun = true
        mThread!!.start()
    }

    fun resumeScroll() {
        if (mThread != null) {
            synchronized(this@MarqueeView) {
                (this@MarqueeView as Object).notify()
                isRun = true
            }
        }
    }

    /**
     * 停止滚动
     */
    fun stopScroll() {
        try {
            if (mThread != null) {
                isRun = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun closeScroll() {
        try {
            if (mThread != null) {
                isRun = false
                mThread!!.interrupt()
                //   holder.getSurface().release();
            }
            mThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 线程
     */
    inner class MarqueeViewThread(private val holder: SurfaceHolder) : Thread() {

        override fun run() {
            while (true) {
                synchronized(this@MarqueeView) {
                    try {
                        if (!isRun) {
                            (this@MarqueeView as Object).wait()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // Log.v("sureface====", "isRun======" + isRun + "===activity====  " + TAG);
                onDraw()
            }
        }


        fun onDraw() {
            synchronized(this) {
                if (TextUtils.isEmpty(margueeString)) {
                    return
                }
                try {
                    canvas = holder.lockCanvas()
                    if (sufaceCreate && canvas != null) {
                        val paddingLeft = paddingLeft
                        val paddingTop = paddingTop
                        val paddingRight = paddingRight
                        val paddingBottom = paddingBottom
                        val contentWidth = getWidth() - paddingLeft - paddingRight
                        val contentHeight = height - paddingTop - paddingBottom
                        val centeYLine = paddingTop + contentHeight / 2//中心线
                        if (mDirection == 0) {//向左滚动
                            if (currentX <= -textWidth) {
                                if (!mIsRepeat) {//如果是不重复滚动
                                    mHandler.sendEmptyMessage(ROLL_OVER)
                                }
                                currentX = contentWidth
                            } else {
                                currentX -= sepX
                            }
                        } else {//  向右滚动
                            if (currentX >= contentWidth) {
                                if (!mIsRepeat) {//如果是不重复滚动
                                    mHandler.sendEmptyMessage(ROLL_OVER)
                                }
                                currentX = -textWidth
                            } else {
                                currentX += sepX
                            }
                        }
                        canvas!!.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                        canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)//绘制透明色
                        canvas!!.drawText(margueeString!!, currentX.toFloat(), (centeYLine + dip2px(context, textHeight.toFloat()) / 2).toFloat(), mTextPaint!!)
                        val a = textWidth / margueeString!!.trim { it <= ' ' }.length
                        val b = a / sepX
                        val c = if (mSpeed / b == 0) 1 else mSpeed / b
                        //     Log.i("sleep======", c + "");
                        if (isRun) {
                            Thread.sleep(c.toLong())//睡眠时间为移动的频率
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (canvas != null && sufaceCreate) {
                            holder.unlockCanvasAndPost(canvas)//结束锁定画图，并提交改变。
                        } else {
                            stopScroll()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    fun unlockCanvasAndPost() {
        try {
            if (canvas != null && holder != null) {
                holder!!.unlockCanvasAndPost(canvas)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun reset() {
        val contentWidth = getWidth() - paddingLeft - paddingRight
        if (mStartPoint == 0)
            currentX = 0
        else
            currentX = contentWidth
    }

    /**
     * 滚动回调
     */
    interface OnMargueeListener {
        fun onRollOver() //滚动完毕
    }

    fun setOnMargueeListener(mOnMargueeListener: MarqueeView.OnMargueeListener) {
        this.mOnMargueeListener = mOnMargueeListener
    }

    companion object {

        val ROLL_OVER = 100

        /**
         * dip转换为px
         *
         * @param context
         * @param dpValue
         * @return
         */
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}
