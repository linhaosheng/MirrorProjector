package com.mirroproject.runnable

import android.util.Log
import com.mirroproject.entity.UpdateInfo
import com.mirroproject.http.MyOkHttpUtil
import com.mirroproject.http.RequeatListener
import com.mirroproject.util.GsonParse
import com.mirroproject.util.SharedPerManager
import org.greenrobot.eventbus.EventBus

/**
 * Created by reeman on 2017/10/31.
 */

/***
 * 网络请求模板
 */
class AppUpdateRunnable(var requestUrl: String) : Runnable, RequeatListener {

    lateinit var listener: RequeatListener
    lateinit var myOkHttpUtil: MyOkHttpUtil

    init {
        myOkHttpUtil = MyOkHttpUtil(requestUrl)
    }

    override fun run() {
        myOkHttpUtil.getRequestInfo(this)
    }

    override fun requestSuccess(json: String) {
        //在这里写json解析，解析完毕，使用EventBus把数据返回给界面
        Log.e("HttpGetRunnable", "===json:" + json)
        val updateInfo = GsonParse.updateParse(json) as UpdateInfo
        SharedPerManager.setApkDownUr(updateInfo.apkurl)
        EventBus.getDefault().post(updateInfo)
        Log.e("HttpGetRunnable", "===updateInfo.toString:" + updateInfo.toString())
    }

    override fun requestFailed(errorDesc: String) {
        //这里吧错误信息返回给界面
        Log.e("HttpGetRunnable", "===网络请求失败=" + errorDesc)
        //              (int appversion, String updatedesc, String apkurl, String webversion)
        val updateInfo = UpdateInfo(-1, errorDesc, "", "1")
    }
}
