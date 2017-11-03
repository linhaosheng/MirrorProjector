package com.mirroproject.activity.main

import android.util.Log
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.util.NetWorkUtils
import com.mirroproject.util.QRCodeUtil
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/3.
 */

class MainActivityBussin {

    fun iniIp() {
        //生成投屏的二维码
        val ip = "http://" + NetWorkUtils.getIP(MirrorApplication.getInstance()) + ":8000"
        Log.i("ip======", ip)
        QRCodeUtil.createErCode(ip, AppInfo.TOU_PING_ER_CODE_PATH, 500)
    }
}