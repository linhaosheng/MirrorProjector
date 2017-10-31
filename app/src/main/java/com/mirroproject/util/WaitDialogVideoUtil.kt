package com.mirroproject.util

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView

/**
 * Created by reeman on 2017/10/31.
 */
class WaitDialogVideoUtil {

    lateinit var waitDialog: Dialog
    lateinit var context: Context
    val TAG = WaitDialogVideoUtil::class.java.name
    lateinit var mTv: TextView

    fun WaitDialogVideoUtil(context: Context) {
        this.context = context
        waitDialog = Dialog(context, R.style.MyDialog)
        val recdialog = View.inflate(context, R.layout.dialog_wait_video, null)
        mTv = recdialog.findViewById<View>(R.id.tv_dialog_wait) as TextView
        waitDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        waitDialog!!.setContentView(recdialog)
        waitDialog!!.setCanceledOnTouchOutside(false)
        waitDialog!!.setCancelable(true)
        val manager = waitDialog!!.window
        manager!!.setGravity(Gravity.LEFT)
        val attributes = manager.attributes
        attributes.x = 400
        manager.attributes = attributes
    }


    fun show(text_dialog: String) {
        dismiss()
        mTv.text = text_dialog
        waitDialog!!.show()
    }

    fun dismiss() {
        if (waitDialog != null && waitDialog!!.isShowing) {
            waitDialog!!.dismiss()
        }
    }

    fun isShowing(): Boolean {
        return if (waitDialog != null && waitDialog!!.isShowing) {
            true
        } else false
    }
}