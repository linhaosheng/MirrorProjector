package com.mirroproject.util

import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo

/**
 * Created by reeman on 2017/10/31.
 */
class SharedPerManager {

    companion object {
        /***
         * 设置屏保的间隔时间
         * 获得是诗分钟，要*60*1000
         * @param distanceTime
         */
        fun setScreenAdTime(distanceTime: Int) {
            MirrorApplication.getInstance().saveData("distanceTime", distanceTime)
        }

        /***
         * 获取屏保的间隔时间，默认一分钟
         * @return
         */
        fun getScreenAdTime(): Int {
            return MirrorApplication.getInstance().getData("distanceTime", 60 * 1000) as Int
        }

        fun getSysDownUrl(): String {
            return MirrorApplication.getInstance().getData("sysdownurl", AppInfo.FIREWARE_URL) as String
        }

        fun setSysDownUrl(sysdownurl: String) {
            MirrorApplication.getInstance().saveData("sysdownurl", sysdownurl)
        }


        fun getApkDownUrl(): String {
            return MirrorApplication.getInstance().getData("apkdownurl", AppInfo.APK_UPDATE_DOWN_URL) as String
        }

        fun setApkDownUr(apkdownurl: String) {
            MirrorApplication.getInstance().saveData("apkdownurl", apkdownurl)
        }


        fun isLogin(): Boolean {
            return MirrorApplication.getInstance().getData("login", false) as Boolean
        }

        fun setLogin(islogin: Boolean) {
            MirrorApplication.getInstance().saveData("login", islogin)
        }

        fun getDeviceId(): String {
            return MirrorApplication.getInstance().getData("deviceId", "") as String
        }

        fun setDeviceId(deviceId: String) {
            MirrorApplication.getInstance().saveData("deviceId", deviceId)
        }

        fun getScreenWidth(): Int {
            return MirrorApplication.getInstance().getData("screenWidth",
                    1366) as Int
        }

        fun setScreenWidth(screenWidth: Int) {
            MirrorApplication.getInstance().saveData("screenWidth", screenWidth)
        }

        fun getScreenHeight(): Int {
            return MirrorApplication.getInstance().getData(
                    "screenHeight", 768) as Int
        }

        fun setScreenHeight(screenHeight: Int) {
            MirrorApplication.getInstance().saveData("screenHeight", screenHeight)
        }

        fun getPassword(): String {
            return MirrorApplication.getInstance().getData("password", "") as String
        }

        fun setPassword(password: String) {
            MirrorApplication.getInstance().saveData("password", password)
        }

        fun getUserName(): String {
            return MirrorApplication.getInstance().getData("username", "") as String
        }

        fun setUserName(username: String) {
            MirrorApplication.getInstance().saveData("username", username)
        }


        fun getLoginInfo(): String {
            return MirrorApplication.getInstance().getData("loginInfo", "") as String
        }

        /***
         * 保存登录信息json信息
         * @param loginInfo
         */
        fun setLoginInfo(loginInfo: String) {
            MirrorApplication.getInstance().saveData("loginInfo", loginInfo)
        }

        fun getToken(): String {
            return MirrorApplication.getInstance().getData("token", "") as String
        }

        fun setToken(token: String) {
            MirrorApplication.getInstance().saveData("token", token)
        }

        fun getMaxVolumn(): Int {
            return MirrorApplication.getInstance().getData("maxVolum", 12) as Int
        }

        fun setMaxVolum(maxVolum: Int) {
            MirrorApplication.getInstance().saveData("maxVolum", maxVolum)
        }
    }

}
