package com.mirroproject.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.mirroproject.R
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.SystemUpdateInfo
import com.mirroproject.entity.UpdateInfo
import com.mirroproject.service.MirrorService
import com.mirroproject.util.APKUtil
import com.mirroproject.util.FileUtil
import com.mirroproject.util.SharedPerManager
import com.mirroproject.util.UpdateParsrer
import com.mirroproject.view.MyToastView
import com.mirroproject.view.RopeProgressBar
import com.mirroproject.view.SystemDialog
import com.mirroproject.view.WaitDialogUtil
import com.mirroproject.view.down.UpdateView
import com.mirroproject.view.down.UpdateView.Companion.TAG_APP_UPDATE
import com.mirroproject.view.down.UpdateView.Companion.TAG_SYSTEM_UPDATE
import kotlinx.android.synthetic.main.activity_update.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by reeman on 2017/11/3.
 */

class UpdateActiivty : BaseActivity(), UpdateView, SystemDialog.SystemDialogListener {

    private var waitDialog: WaitDialogUtil? = null
    private var bind: Unbinder? = null
    private var systemDialog: SystemDialog? = null
    internal var updateParsrer: UpdateParsrer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        EventBus.getDefault().register(this)
        bind = ButterKnife.bind(this)
        updateView()
        MirrorService.getInstance().checkUpdate()
    }

    private fun updateView() {
        FileUtil.creatDirPathNoExists()   //防止文件夹不存在，重新创建一次
        updateParsrer = UpdateParsrer(this@UpdateActiivty, this)
        waitDialog = WaitDialogUtil(this)
        waitDialog!!.show("数据加载中")
        systemDialog = SystemDialog(this)
        systemDialog!!.setSystemDialogListener(this)
    }


    /***
     * 升级图标
     * @param systemUpdateInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateSystem(systemUpdateInfo: SystemUpdateInfo) {
        waitDialog!!.dismiss()
        //去更新界面
        updateParsrer!!.updateSystemView(systemUpdateInfo)
        updateText()
    }

    private fun updateText() {}

    /***
     * 软件升级网络请求返回
     * @param updateInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateApp(updateInfo: UpdateInfo) {
        waitDialog!!.dismiss()
        //去更新界面
        updateParsrer!!.updateAppView(updateInfo)
        updateText()
    }

    @OnClick(R.id.btn_system_update, R.id.btn_update_app)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_system_update -> systemDialog!!.show("是否下载最新系统软件", TAG_SYSTEM_UPDATE)
            R.id.btn_update_app -> systemDialog!!.show("是否下载最新的软件", TAG_APP_UPDATE)
        }
    }

    /***
     * Dialog确认点击回调用
     */
    override fun update(tag: Int) {
        if (tag == TAG_SYSTEM_UPDATE) {  //系统升级点击确认
            val imgDownPath = SharedPerManager.getSysDownUrl()
            val savePath = AppInfo.FIREWARE_DOWNLOAD_SAVE_APK
            lin_progress!!.visibility = View.VISIBLE
            updateParsrer!!.downFileStart(imgDownPath, savePath, TAG_SYSTEM_UPDATE)
        } else if (tag == TAG_APP_UPDATE) {  //软件升级点击下载
            val apkUpdateUrl = SharedPerManager.getApkDownUrl()
            val saveApkPath = AppInfo.DOWNLOAD_SAVE_APK
            lin_progress!!.visibility = View.VISIBLE
            updateParsrer!!.downFileStart(apkUpdateUrl, saveApkPath, TAG_APP_UPDATE)
        }
    }

    /***
     * 下载完成返回的结果在这里处理
     * @param type
     * TAG_SYSTEM_UPDATE
     * @param path
     */
    override fun showToastMainView(type: Int, path: String) {
        lin_progress!!.visibility = View.GONE
        when (type) {
            TAG_SYSTEM_UPDATE -> {  //系统升级
                val intent = Intent()
                intent.action = "android.intent.action.YS_UPDATE_FIRMWARE"
                intent.putExtra("img_path", AppInfo.FIREWARE_DOWNLOAD_SAVE_APK)
                sendBroadcast(intent)
            }
            TAG_APP_UPDATE -> { //软件升级
                APKUtil.installApk(this, path)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateParsrer != null) {
            updateParsrer!!.downStop()
        }
        if (bind != null) {
            bind!!.unbind()
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyToastView.getInstances().Toast(this@UpdateActiivty, "退出界面会暂停下载，请知悉 ！ ")
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getSystemCurrentCode(): TextView {
        return tv_current_systen_code
    }

    override fun getSystemWebCode(): TextView {
        return tv_system_web_code
    }

    override fun getSystemWebDesc(): TextView {
        return tv_system_web_desc
    }

    override fun getBtnSysUpdate(): Button {
        return btn_system_update
    }

    override fun getProgressBar(): RopeProgressBar {
        return update_progress
    }

    override fun getSpeedTv(): TextView {
        return speed
    }

    override fun getDownStateTv(): TextView {
        return tv_down_state
    }

    override fun getLocalCurrenCode(): TextView {
        return tv_current_local_code
    }

    override fun getLocalWebCode(): TextView {
        return tv_local_web_code
    }

    override fun getLocalWebDesc(): TextView {
        return tv_local_web_desc
    }

    override fun getUpdateAppBtn(): Button {
        return btn_update_app
    }

    override fun getLinSoftlayout(): LinearLayout {
        return lin_soft_update
    }

    override fun getLinSyslayout(): LinearLayout {
        return lin_system_update
    }

    override fun getNoUpdateText(): TextView {
        return tv_no_update
    }
}
