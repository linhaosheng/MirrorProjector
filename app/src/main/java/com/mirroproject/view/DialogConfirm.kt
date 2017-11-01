package com.mirroproject.view

import android.content.Context
import com.mirroproject.entity.EventType
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/1.
 */
class DialogConfirm(context: Context) : DownDialog.DownDialogListener {

    internal var downDialog: DownDialog? = null
    private var clickId: Int = 0

    val isShow: Boolean
        get() = if (downDialog != null && downDialog!!.isShow()) {
            true
        } else false


    init {
        downDialog = DownDialog(context)
        downDialog!!.setOnDialogClickListener(this)
    }

    fun show(content: String, clickId: Int) {
        this.clickId = clickId
        if (downDialog != null) {
            downDialog!!.show(content)
        }
    }

    fun setTitle(title: String) {
        if (downDialog != null) {
            downDialog!!.setTitle(title)
        }
    }

    fun dimiss() {
        if (downDialog != null) {
            downDialog!!.dissmiss()
        }
    }

    fun setBtnText(sure: String, noSure: String) {
        if (downDialog != null) {
            downDialog!!.setBtnText(sure, noSure)
        }
    }

    fun hideSpeed() {
        if (downDialog != null) {
            downDialog!!.hideSpeed()
        }
    }

    override fun noSure() {
        if (downDialog != null) {
            downDialog!!.dissmiss()
        }
    }

    override fun sure() {
        EventBus.getDefault().post(EventType.ExitType(clickId))
    }
}
