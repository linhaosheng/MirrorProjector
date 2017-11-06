package com.mirroproject.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.mirroproject.R
import com.mirroproject.util.SharedPerManager

/**
 * Created by reeman on 2017/11/6.
 */

class DialogMessage(internal var context: Context) : View.OnHoverListener {
    internal var btn_no: Button
    internal val btn_dialog_yes: Button
    var dialog: Dialog? = null
    var dialogClick: DialogMessageListener? = null
    var tv_title: TextView
    var max_volum: SeekBar
    var current_volumn: TextView

    private val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.setBackgroundColor(Color.BLUE)
        } else {
            v.setBackgroundColor(-0x4c4c4d)
        }
    }

    val isShow: Boolean
        get() = if (dialog != null && dialog!!.isShowing) {
            true
        } else false

    init {
        if (dialog == null) {
            dialog = Dialog(context, R.style.MyDialog)
        }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val width = SharedPerManager.getScreenWidth()
        val height = SharedPerManager.getScreenHeight()
        val params = RelativeLayout.LayoutParams(width, height)
        val dialog_view = View.inflate(context, R.layout.dialog_message, null)
        dialog!!.setContentView(dialog_view, params)
        val manager = dialog!!.window
        manager!!.setGravity(Gravity.TOP)


        dialog!!.setCancelable(false) // true点击屏幕以外关闭dialog
        current_volumn = dialog_view.findViewById<View>(R.id.current_volumn) as TextView
        current_volumn.text = "当前声道 " + SharedPerManager.getMaxVolumn()
        tv_title = dialog_view.findViewById<View>(R.id.dialog_title) as TextView
        max_volum = dialog_view.findViewById<View>(R.id.max_volum) as SeekBar
        max_volum.max = 15
        max_volum.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                current_volumn.text = "当前声道 " + seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        btn_no = dialog_view.findViewById<View>(R.id.btn_dialog_no) as Button
        btn_dialog_yes = dialog_view.findViewById<View>(R.id.btn_dialog_yes) as Button
        btn_no.setOnHoverListener(this)
        btn_dialog_yes.setOnHoverListener(this)
        btn_dialog_yes.setOnClickListener { dialogClick!!.sure(max_volum.progress) }

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

    fun setCurrentProgress(progress: Int) {
        max_volum.progress = progress
    }

    fun setDialogGravity() {
        val manager = dialog!!.window
        manager!!.setGravity(Gravity.CENTER)
    }

    fun show(desc: String) {
        dialog!!.show()
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setBtnText(sure: String, noSure: String) {
        btn_dialog_yes.text = sure
        btn_no.text = noSure
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

    fun setOnDialogClickListener(dc: DialogMessageListener) {
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

    interface DialogMessageListener {
        fun noSure()

        fun sure(maxVolum: Int)
    }
}
