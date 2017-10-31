package com.mirroproject.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import com.mirroproject.util.ActivityCollector
import com.mirroproject.util.NetWorkUtils
import com.mirroproject.view.MyToastView

/**
 * Created by reeman on 2017/10/30.
 */
abstract class BaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ActivityCollector.addActivity(this)

    }

    override fun onResume() {
        super.onResume()
        if (!NetWorkUtils.isNetworkConnected(this)) {
            MyToastView.getInstances().Toast(this, "网络异常，请检查")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}