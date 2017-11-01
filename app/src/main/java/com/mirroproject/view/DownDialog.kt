package com.mirroproject.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.mirroproject.R
import com.mirroproject.util.SharedPerManager

/**
 * Created by reeman on 2017/11/1.
 */
class DownDialog(var context: Context) : View.OnHoverListener {
    private var dialog: Dialog? = null
    var dialogClick: DownDialogListener? = null
    var view_progress: RopeProgressBar
    var tv_title: TextView
    var tv_speed: TextView
    private val tv_desc_update: TextView

    private val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.setBackgroundColor(Color.BLUE)
        } else {
            v.setBackgroundColor(-0x4c4c4d)
        }
    }


    var btn_no: Button
    val btn_dialog_yes: Button


    fun isShow(): Boolean {
        if (dialog != null && dialog!!.isShowing) {
            return true
        }
        return false
    }

    /***
     * 刷新dialog
     *
     * @param progress
     * 进度
     * @param desc
     * 下载秒速
     * @param speed
     * 下载速度
     */
    fun updateView(progress: Int, desc: String, speed: Long) {
        view_progress.progress = progress
        tv_title.setTextColor(-0xe0450d)
        tv_title.text = "文件下载($desc)"
        tv_speed.text = (speed).toString() + " kb/s"
        if (desc.contains("异常")) {
            tv_title.setTextColor(Color.RED)
            tv_title.text = "下载异常，请重新启动程序"
        }
    }

    init {
        if (dialog == null) {
            dialog = Dialog(context, R.style.MyDialog)
        }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val width = SharedPerManager.getScreenWidth()
        val height = SharedPerManager.getScreenHeight()
        val params = RelativeLayout.LayoutParams(width, height)
        val dialog_view = View.inflate(context, R.layout.dialog_down, null)
        dialog!!.setContentView(dialog_view, params)

        dialog!!.setCancelable(false) // true点击屏幕以外关闭dialog
        tv_title = dialog_view.findViewById<View>(R.id.dialog_title) as TextView
        tv_desc_update = dialog_view.findViewById<View>(R.id.tv_desc_update) as TextView
        tv_speed = dialog_view.findViewById<View>(R.id.tv_speed) as TextView
        btn_no = dialog_view.findViewById<View>(R.id.btn_dialog_no) as Button
        btn_dialog_yes = dialog_view.findViewById<View>(R.id.btn_dialog_yes) as Button
        btn_no.setOnHoverListener(this)
        btn_dialog_yes.setOnHoverListener(this)
        view_progress = dialog_view.findViewById<View>(R.id.update_progress) as RopeProgressBar
        btn_dialog_yes.setOnClickListener {
            view_progress.visibility = View.VISIBLE
            tv_desc_update.visibility = View.INVISIBLE
            btn_dialog_yes.visibility = View.GONE
            dialogClick!!.sure()
        }

        btn_no.setOnClickListener {
            if (dialogClick != null) {
                dialogClick!!.noSure()
                dissmiss()
            }
        }

        btn_no.setBackgroundColor(-0x4c4c4d)
        btn_dialog_yes.setBackgroundColor(-0x4c4c4d)
        btn_no.onFocusChangeListener = onFocusChangeListener
        btn_dialog_yes.onFocusChangeListener = onFocusChangeListener
    }

    fun show(desc: String?) {
        view_progress.visibility = View.INVISIBLE
        tv_desc_update.visibility = View.VISIBLE
        if (desc != null && desc.length > 4) {
            tv_desc_update.text = desc
        }
        tv_speed.text = "0kb/s"
        dialog!!.show()
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setBtnText(sure: String, noSure: String) {
        btn_dialog_yes.text = sure
        btn_no.text = noSure
    }

    fun hideSpeed() {
        tv_speed.visibility = View.GONE
    }

    fun dissmiss() {
        if (dialog == null) {
            return
        }
        btn_dialog_yes.visibility = View.VISIBLE
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    fun setOnDialogClickListener(dc: DownDialogListener) {
        dialogClick = dc
    }

    override fun onHover(view: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action
        when (action) {
            MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
            -> view.setBackgroundResource(R.drawable.rect_circle_blue)
            MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
            -> view.setBackgroundResource(R.drawable.rect_circle_grey)
        }
        return false
    }

    interface DownDialogListener {
        fun noSure()
        fun sure()
    }
}
