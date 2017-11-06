package com.mirroproject.util

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.mirroproject.entity.DownFileEntity
import com.mirroproject.entity.SystemUpdateInfo
import com.mirroproject.entity.UpdateInfo
import com.mirroproject.runnable.DownRunnable
import com.mirroproject.runnable.DownStateListener
import com.mirroproject.view.MyToastView
import com.mirroproject.view.RopeProgressBar
import com.mirroproject.view.down.UpdateView

/**
 * Created by reeman on 2017/11/6.
 */

class UpdateParsrer(internal var context1: Context, internal var updateView1: UpdateView) : DownStateListener {

    lateinit var updateView: UpdateView
    lateinit var tv_system_web_code: TextView
    lateinit var tv_system_web_desc: TextView
    lateinit var tv_current_systen_code: TextView
    lateinit var btn_system_update: Button
    var context: Context? = null
    lateinit var downRunnable: DownRunnable
    lateinit var ropeProgressBar: RopeProgressBar
    lateinit var tv_down_state: TextView
    lateinit var tv_speed: TextView

    lateinit var tv_current_local_code: TextView
    lateinit var tv_local_web_code: TextView
    lateinit var tv_local_web_desc: TextView
    lateinit var btn_update_app: Button
    lateinit var lin_system_update: LinearLayout
    lateinit var lin_soft_update: LinearLayout
    lateinit var tv_no_update: TextView


    /***
     * 获取本地系统版本号
     * @return
     */
    val systemCode: String
        get() = Build.VERSION.INCREMENTAL.trim { it <= ' ' }

    internal var downType: Int = 0


    init {
        getView()
    }

    /***
     * 获取主界面的控件
     */
    private fun getView() {
        tv_no_update = updateView.getNoUpdateText()
        lin_system_update = updateView.getLinSyslayout()
        lin_soft_update = updateView.getLinSoftlayout()

        tv_system_web_code = updateView.getSystemWebCode()
        tv_system_web_desc = updateView.getSystemWebDesc()
        tv_current_systen_code = updateView.getSystemCurrentCode()
        btn_system_update = updateView.getBtnSysUpdate()
        btn_system_update.isClickable = false  //默认不可点击
        ropeProgressBar = updateView.getProgressBar()
        tv_down_state = updateView.getDownStateTv()
        tv_speed = updateView.getSpeedTv()

        tv_current_local_code = updateView.getLocalCurrenCode()
        tv_local_web_code = updateView.getLocalWebCode()
        tv_local_web_desc = updateView.getLocalWebDesc()
        btn_update_app = updateView.getUpdateAppBtn()
        btn_update_app.isClickable = false  //默认不可点击 ，网络请求回来了才可以点击
    }

    fun updateAppView(updateInfo: UpdateInfo) {
        val webApCode = updateInfo.webversion
        val appDesc = updateInfo.updatedesc
        tv_current_local_code.text = APKUtil.getVersionName(context!!)
        tv_local_web_code.setText(webApCode)
        tv_local_web_desc.setText(appDesc)

        //本地软件版本号
        lin_soft_update.visibility = View.GONE
        btn_update_app.isClickable = false
        val apCodeCurrent = APKUtil.getVersionCode(context!!)
        val webCode = updateInfo.appversion
        if (webCode > apCodeCurrent) {  //显示升级
            btn_update_app.isClickable = true
            btn_update_app.text = "软件升级"
            lin_soft_update.visibility = View.VISIBLE
        } else {
            updateText()
            btn_update_app.text = "(软件升级)当前为最新版本"
        }
    }

    /***
     * 如果都不需要更新，显示这一段话
     */
    private fun updateText() {
        if (lin_soft_update.visibility == View.GONE && lin_system_update.visibility == View.GONE) {
            tv_no_update.visibility = View.VISIBLE
        }
    }


    fun updateSystemView(systemUpdateInfo: SystemUpdateInfo) {
        lin_system_update.visibility = View.GONE
        btn_system_update.isClickable = false
        val systemcode = systemUpdateInfo.systemcode
        val updatedesc = systemUpdateInfo.updatedesc
        val apkdown = systemUpdateInfo.apkdown
        tv_current_systen_code.text = Build.VERSION.INCREMENTAL.trim { it <= ' ' }.trim { it <= ' ' }
        if (systemcode == null || systemcode!!.length < 5) {  //网络请求失败,异常
            tv_current_systen_code.text = "网络请求异常，请检查网络"
            MyToastView.getInstances().Toast(context!!, systemUpdateInfo.updatedesc)
            return
        } else {
            tv_system_web_code.setText(systemcode)
            tv_system_web_desc.setText(updatedesc)
        }
        if (systemCode == systemcode) {
            btn_system_update.text = "(系统升级)当前是最新版本"
            updateText()
        } else {
            lin_system_update.visibility = View.VISIBLE
            btn_system_update.isClickable = true
            btn_system_update.text = "系统升级"
        }
    }

    fun downFileStart(downUrl: String, savePath: String, type: Int) {
        this.downType = type
        if (!NetWorkUtils.isNetworkConnected(context!!)) {
            Toast.makeText(context, "网络异常，请检查", Toast.LENGTH_LONG).show()
            return
        }
        downRunnable = DownRunnable(downUrl, savePath, this as DownStateListener)
        val thread = Thread(downRunnable)
        thread.start()
    }

    fun downStop() {
        if (downRunnable != null) {
            downRunnable!!.stopDown()
        }
    }

    override fun downStateInfo(entity: DownFileEntity) {
        updateState(entity)
        tv_speed.setText(entity.downSpeed.toString() + " KB")
        if (Math.abs(entity.progress) > 100) {   //防止第一次下载获取不到进度条的问题
            downStop()
            MyToastView.getInstances().Toast(context!!, "未获取到文件，请点击重试 ，多次出现请重启 。")
        }
        ropeProgressBar.progress = entity.progress
    }

    private fun updateState(entity: DownFileEntity) {
        val downState = entity.downState
        when (downState) {
            DownFileEntity.DOWN_STATE_START -> {
                btn_system_update.isClickable = false
                btn_update_app.isClickable = false
                tv_down_state.text = "开始下载"
            }
            DownFileEntity.DOWN_STATE_PROGRESS -> {
                btn_system_update.isClickable = false
                btn_update_app.isClickable = false
                tv_down_state.text = "下载中"
            }
            DownFileEntity.DOWN_STATE_SUCCESS -> {
                btn_system_update.isClickable = true
                btn_update_app.isClickable = true
                tv_down_state.text = "下载成功"
                //下载完成，吧结果返回给主界面
                updateView.showToastMainView(downType, entity.savePath)
            }
            DownFileEntity.DOWN_STATE_FAIED -> {
                btn_system_update.isClickable = true
                btn_update_app.isClickable = true
                tv_down_state.text = "下载失败,请重试，重复多次出现请重启设备"
            }
            DownFileEntity.DOWN_STATE_CACLE -> {
                btn_system_update.isClickable = true
                btn_update_app.isClickable = true
                tv_down_state.text = "未获取到文件，请点击重试 ! 重复多次出现请重启设备"
            }
        }
    }
}
