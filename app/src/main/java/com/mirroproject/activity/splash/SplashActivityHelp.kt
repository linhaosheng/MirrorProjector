package com.mirroproject.activity.splash

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import com.mirroproject.R
import com.mirroproject.activity.LoginActivity
import com.mirroproject.activity.main.MainActivity
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.EventType
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.RetrofitFactory
import com.mirroproject.service.MirrorService
import com.mirroproject.service.PopupService
import com.mirroproject.util.FileUtil
import com.mirroproject.util.NetWorkUtils
import com.mirroproject.util.PlayerUtil
import com.mirroproject.view.MyToastView
import com.mirroproject.view.WaitDialogUtil
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */
class SplashActivityHelp {
    private var isLoginSuccess: Boolean = false

    var splashActivity: SplashActivity
    var splashActivityView: SplashActivityView
    @Inject
    lateinit var splashActivityBussin: SplashActivityBussin
    @Inject
    lateinit var retrofitFactory: RetrofitFactory
    @Inject
    lateinit var player: PlayerUtil
    @Inject
    lateinit var waitDialogUtil: WaitDialogUtil
    @Inject
    lateinit var httpRequest: HttpRequest

    var handler: Handler? = null

    constructor(splashActivity: SplashActivity, handler: Handler) {
        this.splashActivity = splashActivity
        this.splashActivityView = splashActivity
        this.handler = handler
        //  DaggerSplashActivityComponent.builder().splashActivityModule(SplashActivityModule(splashActivity, handler)).appModule(AppModule(splashActivity)).build().inject(this)
        splashActivityBussin!!.generateEr()
        initView()
    }

    fun deleteAndGetData() {
        //每次开机都删除一次升级文件，不能删除这两行代码
        FileUtil.deleteDirOrFile(AppInfo.FIREWARE_DOWNLOAD_SAVE_APK)
        FileUtil.deleteDirOrFile(AppInfo.DOWNLOAD_SAVE_APK)
        splashActivityBussin!!.getAdvData()  //获取gif广告连接
        splashActivityBussin!!.getScreenData()  //获取屏保
        //  MirrorService.getInstance().getGifAdList();   //获取gif广告连接
    }

    fun saveSize() {
        splashActivityBussin!!.saveScreenSize(splashActivity)
    }

    fun palyFinsh(playEventType: EventType.PlayEventType) {
        if (playEventType.palyType === 2) {
            if (isLoginSuccess) {
                splashActivity.startActivity(Intent(splashActivity, MainActivity::class.java))
            } else {
                splashActivity.startActivity(Intent(splashActivity, LoginActivity::class.java))
            }
            splashActivity.finish()
            Log.i(TAG, "====播放完毕了要跳转页面了=")
        }
    }

    fun isLogin(loginType: EventType.LoginType) {
        if (loginType.loginType === EventType.LoginType.LOGIN_SUCCESS) {
            isLoginSuccess = true
            MyToastView.getInstances().Toast(splashActivity, "自动登录成功")
            splashActivity.startService(Intent(splashActivity, MirrorService::class.java))
            splashActivity.startService(Intent(splashActivity, PopupService::class.java))  //初始化Service
            splashActivityBussin!!.openMonitor()
        } else if (loginType.loginType === EventType.LoginType.LOGIN_ERROR) {
            handler!!.sendEmptyMessageDelayed(DELAY_LOGIN, 4000)
            isLoginSuccess = false
        }
        Log.i("autoLogin=====", isLoginSuccess.toString() + "")
    }

    fun setVideoLayout(type: EventType.SetVideoLayoutType) {
        if (type.videoType === 1) {
            Log.i(TAG, "setVideoLayout=====")
            val mediaPlayer = player!!.mediaPlayer
            if (mediaPlayer != null) {
                val videoWidth = mediaPlayer!!.getVideoWidth()
                val videoHeight = mediaPlayer!!.getVideoHeight()
                val layoutParams = splashActivityView.getSurFaceView().getLayoutParams() as LinearLayout.LayoutParams
                layoutParams.width = videoWidth
                layoutParams.height = videoHeight
                splashActivityView.getSurFaceView().setLayoutParams(layoutParams)
                Log.i(TAG, "end=====$videoWidth====height$videoHeight")
            }
        }
    }


    private fun initView() {
        player!!.setLoaclVideo(true)
        waitDialogUtil!!.show("启动中....")
        Log.i("state====", NetWorkUtils.isWifiAvailable(splashActivity).toString())
        if (!NetWorkUtils.isWifiAvailable(splashActivity)) {
            handler!!.sendEmptyMessageDelayed(DELAY_LOGIN, 4000)
        } else {
            MirrorService.getInstance().autoLogin()
        }
    }

    fun onPause() {
        if (player != null) {
            player!!.stop()
        }
        if (waitDialogUtil != null) {
            waitDialogUtil!!.dismiss()
        }
        saveSize()
    }

    fun loadVideo() {
        if (waitDialogUtil != null) {
            waitDialogUtil!!.dismiss()
        }
        val mUri = Uri.parse("android.resource://" + MirrorApplication.getInstance().getPackageName() + "/" + R.raw.start)
        player!!.playUrlHide(mUri)
    }

    fun delayLogin() {
        Log.i("state====1111", NetWorkUtils.isWifiAvailable(splashActivity).toString())
        MirrorService.getInstance().autoLogin()
    }

    companion object {

        private val TAG = "SplashActivityHelp==="
        private val LOAD_VIDEO = 0x11
        private val DELAY_LOGIN = 0x12
    }
}
