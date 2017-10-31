package com.mirroproject.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import com.mirroproject.R

/**
 * Created by reeman on 2017/10/31.
 */
class WaitDialogUtil {
    internal var waitDialog: Dialog? = null
    val TAG = WaitDialogUtil::class.java.name
    internal var mTv: TextView
    internal var context: Context

    constructor(context: Context) {
        this.context = context
        waitDialog = Dialog(context, R.style.MyDialog)
        val recdialog = View.inflate(context, R.layout.dialog_wait, null)
        mTv = recdialog.findViewById<View>(R.id.tv_dialog_wait) as TextView
        waitDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        waitDialog!!.setContentView(recdialog)
        waitDialog!!.setCanceledOnTouchOutside(false)
        waitDialog!!.setCancelable(true)
        waitDialog!!.window!!.setGravity(Gravity.CENTER)
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