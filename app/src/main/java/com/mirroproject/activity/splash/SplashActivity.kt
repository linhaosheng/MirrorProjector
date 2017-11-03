package com.mirroproject.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.SurfaceView
import android.widget.SeekBar
import butterknife.BindView
import butterknife.ButterKnife
import com.mirroproject.activity.BaseActivity
import com.mirroproject.R
import com.mirroproject.activity.LoginActivity
import com.mirroproject.app.AppModule
import com.mirroproject.entity.EventType
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.RetrofitFactory
import com.mirroproject.service.MirrorService
import com.mirroproject.view.WaitDialogUtil
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/30.
 */
class SplashActivity : BaseActivity(), SplashActivityView {

    val TAG = "SplashActivity"
    private val LOAD_VIDEO = 0x11
    private val DELAY_LOGIN = 0x12

    @Inject
    lateinit var splashActivityHelp: SplashActivityHelp

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LOAD_VIDEO -> splashActivityHelp!!.loadVideo()
                DELAY_LOGIN -> {
                    splashActivityHelp!!.delayLogin()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)
        DaggerSplashActivityComponent.builder().splashActivityModule(SplashActivityModule(this, handler)).appModule(AppModule(this)).build().inject(this)
        EventBus.getDefault().register(this)
        handler!!.sendEmptyMessageDelayed(LOAD_VIDEO, 2000)
        handler!!.sendEmptyMessageDelayed(DELAY_LOGIN, 4000)
        startService(Intent(this@SplashActivity, MirrorService::class.java))
    }

    override fun onResume() {
        super.onResume()
        splashActivityHelp!!.deleteAndGetData()
    }

    override fun onPause() {
        super.onPause()
        splashActivityHelp!!.onPause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun palyFinsh(playEventType: EventType.PlayEventType) {
        splashActivityHelp!!.palyFinsh(playEventType)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setVideoLayout(type: EventType.SetVideoLayoutType) {
        splashActivityHelp!!.setVideoLayout(type)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isLogin(loginType: EventType.LoginType) {
        splashActivityHelp!!.isLogin(loginType)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (handler != null) {
            handler!!.removeMessages(LoginActivity.LOGIN_SUCCESS)
            handler!!.removeMessages(LoginActivity.LOGIN_FAILED)
        }
    }

    override fun getSeekBar(): SeekBar {
        return skbProgress
    }

    override fun getSurFaceView(): SurfaceView {
        return surfaceView1
    }
}