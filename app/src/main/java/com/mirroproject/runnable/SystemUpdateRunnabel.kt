package com.mirroproject.runnable

import android.util.Log
import com.mirroproject.entity.SystemUpdateInfo
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
class SystemUpdateRunnabel(internal var requestUrl: String) : Runnable, RequeatListener {

    lateinit var listener: RequeatListener
    internal var myOkHttpUtil: MyOkHttpUtil

    init {
        myOkHttpUtil = MyOkHttpUtil(requestUrl)
    }

    override fun run() {
        myOkHttpUtil.getRequestInfo(this)
    }

    override fun requestSuccess(json: String) {
        //在这里写json解析，解析完毕，使用EventBus把数据返回给界面
        Log.e("HttpGetRunnable", "===json:" + json)
        val updateSystemParse = GsonParse.updateSystemParse(json) as SystemUpdateInfo
        SharedPerManager.setSysDownUrl(updateSystemParse.apkdown)
        EventBus.getDefault().post(updateSystemParse)
        Log.e("HttpGetRunnable", "===systemParse:" + updateSystemParse.toString())
    }

    override fun requestFailed(errorDesc: String) {
        //这里吧错误信息返回给界面
        Log.e("HttpGetRunnable", "===请求失败===" + errorDesc)
        //        SystemUpdateInfo(String systemcode, String updatedesc, String apkdown)
        val systemParse = SystemUpdateInfo("300", errorDesc, "")
        EventBus.getDefault().post(systemParse)
    }
}
