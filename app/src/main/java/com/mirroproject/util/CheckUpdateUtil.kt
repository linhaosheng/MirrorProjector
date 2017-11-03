package com.mirroproject.util

import android.os.Build
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.EventType
import com.mirroproject.entity.SystemUpdateInfo
import com.mirroproject.entity.UpdateInfo
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.HttpRequestListener
import org.greenrobot.eventbus.EventBus

/**
 * Created by reeman on 2017/11/3.
 */

class CheckUpdateUtil(private val httpRequest: HttpRequest) {
    lateinit var systemUpdateInfo: SystemUpdateInfo
    lateinit var updateInfo: UpdateInfo

    fun check(systemUrl: String, sofewareUrl: String) {
        httpRequest.httpGet(systemUrl, object : HttpRequestListener {
            override fun requestSuccess(jsonRequest: String) {
                try {
                    systemUpdateInfo = GsonParse.updateSystemParse(jsonRequest) as SystemUpdateInfo
                    val currentVersion = Build.VERSION.INCREMENTAL.trim { it <= ' ' }
                    if (!systemUpdateInfo.systemcode.equals(currentVersion)) {
                        EventBus.getDefault().post(EventType.UpdateEventType(SYSTEM_UPDATE))
                    } else {
                        checkSofeUpdate(sofewareUrl)
                    }
                } catch (e: Exception) {
                    EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
                    e.printStackTrace()
                }

            }

            override fun requestFailed(error: String) {
                EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
            }
        })
    }

    fun checkSofeUpdate(sofewareUrl: String) {
        httpRequest.httpGet(sofewareUrl, object : HttpRequestListener {
            override fun requestSuccess(jsonRequest: String) {
                try {
                    updateInfo = GsonParse.updateParse(jsonRequest) as UpdateInfo
                    val versionCode = APKUtil.getVersionCode(MirrorApplication.getInstance().getContext()!!)
                    if (updateInfo.appversion > versionCode) {
                        FileUtil.deleteDirOrFile(AppInfo.DOWNLOAD_SAVE_APK)
                        EventBus.getDefault().post(EventType.UpdateEventType(SOFE_UPDATE))
                    }
                } catch (e: Exception) {
                    EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
                    e.printStackTrace()
                }

            }

            override fun requestFailed(error: String) {
                EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
            }
        })
    }

    fun checkAllSofe(systemUrl: String, sofewareUrl: String) {
        httpRequest.httpGet(systemUrl, object : HttpRequestListener {
            override fun requestSuccess(jsonRequest: String) {
                try {
                    systemUpdateInfo = GsonParse.updateSystemParse(jsonRequest) as SystemUpdateInfo
                    checkUpdate(sofewareUrl)
                } catch (e: Exception) {
                    EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
                    e.printStackTrace()
                }

            }

            override fun requestFailed(error: String) {
                EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
            }

        })
    }

    fun checkUpdate(sofewareUrl: String) {
        httpRequest.httpGet(sofewareUrl, object : HttpRequestListener {
            override fun requestSuccess(jsonRequest: String) {
                try {
                    updateInfo = GsonParse.updateParse(jsonRequest) as UpdateInfo
                } catch (e: Exception) {
                    EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
                    e.printStackTrace()
                }

            }

            override fun requestFailed(error: String) {
                EventBus.getDefault().post(EventType.UpdateEventType(UPDATE_FAIL))
            }
        })
    }

    companion object {
        val SYSTEM_UPDATE = 0
        val SOFE_UPDATE = 1
        val UPDATE_FAIL = 2
        val UPDATE_NEW = 3
        val UPDATE_SHOW = 4
        val SOFE_UPDATE_CHECK = 5
    }
}
