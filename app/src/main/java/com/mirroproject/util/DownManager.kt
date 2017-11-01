package com.mirroproject.util

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.mirroproject.runnable.DownListener
import com.mirroproject.view.DownDialog

/**
 * Created by reeman on 2017/11/1.
 */
class DownManager(internal var context: Context) : DownListener, DownDialog.DownDialogListener {
    lateinit var downDialog: DownDialog
    lateinit var downUtil: DownUtil

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                DOWN_START -> downDialog.updateView(0, "准备下载", 0)
                DOWN_PROGRESS -> {
                    val progress = msg.arg1
                    val speed = msg.obj as Long
                    downDialog.updateView(progress, "下载中", speed)
                }
                DOWN_SUCCESS -> {
                    downDialog.updateView(100, "下载完成", 0)
                    downDialog.dissmiss()
                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
                    APKUtil.installApk(context, downPath)
                }
                DOWN_FAILED -> downDialog.updateView(0, "下载异常", 0)
            }
        }
    }

    /***
     * 开始下载
     * @param downUrl
     * 下载地址
     * @param downPath
     * 保存的地址
     * @param desc
     * dialog显示的描述
     */
    lateinit var downUrl: String
    lateinit var downPath: String

    init {
        downDialog = DownDialog(context)
        downDialog.setOnDialogClickListener(this)
        downUtil = DownUtil(this)
    }

    fun downSatrt(downUrl: String, downPath: String, desc: String) {
        this.downUrl = downUrl
        this.downPath = downPath
        downDialog.show(desc)
    }

    override fun downStart() {
        handler.sendEmptyMessage(DOWN_START)
    }

    override fun downProgress(progress: Int, speed: Long) {
        val msg = Message()
        msg.what = DOWN_PROGRESS
        msg.arg1 = progress
        msg.obj = speed
        handler.sendMessage(msg)
    }

    override fun downSuccess(downUrl: String) {
        handler.sendEmptyMessage(DOWN_SUCCESS)

    }

    override fun downFailed(failedDesc: String) {
        handler.sendEmptyMessage(DOWN_FAILED)
    }

    override fun noSure() {
        downUtil.cacleDown()
    }

    override fun sure() {
        downUtil.downFile(downUrl, downPath)
    }

    companion object {

        val DOWN_START = 0
        val DOWN_PROGRESS = DOWN_START + 1
        val DOWN_SUCCESS = DOWN_PROGRESS + 1
        val DOWN_FAILED = DOWN_SUCCESS + 1
    }
}
