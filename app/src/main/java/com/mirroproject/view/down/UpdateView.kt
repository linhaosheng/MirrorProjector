package com.mirroproject.view.down

import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.mirroproject.view.RopeProgressBar

/**
 * Created by reeman on 2017/11/3.
 */

/***
 * 升级界面用来更新界面的View
 */
interface UpdateView {

    fun getSystemCurrentCode(): TextView  //获取本地服务器控

    fun getSystemWebCode(): TextView      //获取服务器版本控件

    fun getSystemWebDesc(): TextView     //获取服务器版本升级描述

    fun getBtnSysUpdate(): Button        //获取系统按控件

    fun getProgressBar(): RopeProgressBar  //进度条控件

    fun getSpeedTv(): TextView          //获取下载速度控件

    fun getDownStateTv(): TextView    //获取下载的状态控件

    //====================================================
    fun getLocalCurrenCode(): TextView   //软件升级，本地显示版本号

    fun getLocalWebCode(): TextView       //软件升级，服务器版本号

    fun getLocalWebDesc(): TextView       //软件升级，升级提示

    fun getUpdateAppBtn(): Button          //软件升级Btn

    fun getLinSoftlayout(): LinearLayout    //软件布局

    fun getLinSyslayout(): LinearLayout   //系统升级布局

    fun getNoUpdateText(): TextView   //系统升级布局

    /***
     * 用来通知主界面的UI更新
     * @param type
     * TAG_SYSTEM_UPDATE
     * @param path
     * 返回的文件的路径
     */
    fun showToastMainView(type: Int, path: String)   //用来通知主界面

    companion object {
        val TAG_SYSTEM_UPDATE = 1245
        val TAG_APP_UPDATE = 1289
    }
}
