package com.mirroproject.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.mirroproject.R
import com.mirroproject.util.SharedPerManager
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/1.
 */
class SystemDialog(private val context: Context) : View.OnHoverListener {
    private val dialog: Dialog?
    private val update_info: TextView
    private val cancel: Button
    private val update: Button
    private var systemDialogListener: SystemDialogListener? = null
    internal var TAGSHOW = -1

    private val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.setBackgroundColor(Color.BLUE)
        } else {
            v.setBackgroundColor(-0x4c4c4d)
        }
    }

    val isShowing: Boolean
        get() = if (dialog != null && dialog!!.isShowing) {
            true
        } else false


    init {
        dialog = Dialog(context, R.style.MyDialog)
        val recdialog = View.inflate(context, R.layout.system_update_dialog, null)
        val width = SharedPerManager.getScreenWidth()
        val height = SharedPerManager.getScreenHeight()
        update_info = recdialog.findViewById<View>(R.id.update_info) as TextView
        cancel = recdialog.findViewById<View>(R.id.cancel) as Button
        update = recdialog.findViewById<View>(R.id.update) as Button
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val params = LinearLayout.LayoutParams(width, height)
        dialog.setContentView(recdialog, params)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.window!!.setGravity(Gravity.CENTER)

        cancel.setOnClickListener { dismiss() }
        update.setOnClickListener {
            dismiss()
            systemDialogListener!!.update(TAGSHOW)
        }
        cancel.setOnHoverListener(this)
        update.setOnHoverListener(this)
        cancel.setBackgroundColor(-0x4c4c4d)
        update.setBackgroundColor(-0x4c4c4d)
        cancel.onFocusChangeListener = onFocusChangeListener
        update.onFocusChangeListener = onFocusChangeListener
    }

    fun setSystemDialogListener(systemDialogListener: SystemDialogListener) {
        this.systemDialogListener = systemDialogListener
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

    interface SystemDialogListener {
        fun update(tag: Int)
    }

    fun show(text_dialog: String, tag: Int) {
        dismiss()
        TAGSHOW = tag
        update_info.text = text_dialog
        dialog!!.show()
    }

    fun setSureButton(title: String) {
        update.text = title
    }

    fun dismiss() {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
