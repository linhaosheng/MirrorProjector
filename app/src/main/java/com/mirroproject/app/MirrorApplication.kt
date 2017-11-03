package com.mirroproject.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.*
import com.mirroproject.service.MirrorService
import com.mirroproject.util.FileUtil
import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by reeman on 2017/10/31.
 */
class MirrorApplication : Application() {

    var mSharedPreferences: SharedPreferences? = null
    var USER_INFO = "userInfo"
    var list_hair1: List<VideoEntity.DataBean>? = null
    var list_finger1: List<VideoEntity.DataBean>? = null
    var list_body1: List<VideoEntity.DataBean>? = null
    var list_anim1: List<VideoEntity.DataBean>? = null
    var tvAbroadInfos1: List<ItemTv>? = null//封装直播的集合
    var liveEntityList1: List<LiveEntity>? = null
    lateinit var context1: Context
    var video_advs1: HashMap<String, String>? = null
    var pos_31: List<ADInfo.Pos3Bean> = ArrayList()
    var infos1: List<ADInfo.Pos1Bean> = ArrayList()
    lateinit var picAdvEntity1: PicAdvEntity
    var screenAdvEntity: ScreenAdvEntity? = null

    companion object {
        var instance1: MirrorApplication? = null
        fun getInstance(): MirrorApplication {
            return instance1!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance1 = this
        context1 = applicationContext
        mSharedPreferences = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE)
        initOther()
    }

    fun setPicAdvEntity(picAdvEntity: PicAdvEntity) {
        this.picAdvEntity1 = picAdvEntity
    }

    fun getPicAdvEntity(): PicAdvEntity {
        return picAdvEntity1
    }

    fun setPos_3(pos_3: List<ADInfo.Pos3Bean>) {
        this.pos_31 = pos_3
    }

    fun setInfos(infos: List<ADInfo.Pos1Bean>) {
        this.infos1 = infos
    }

    fun getInfos(): List<ADInfo.Pos1Bean> {
        return infos1
    }

    fun getPos_3(): List<ADInfo.Pos3Bean> {
        return pos_31
    }

    fun getContext(): Context? {
        return context1
    }

    fun initOther() {
        FileUtil.creatDirPathNoExists()
        //列表封装
        list_hair1 = ArrayList()
        list_finger1 = ArrayList()
        list_body1 = ArrayList()
        list_anim1 = ArrayList()
        tvAbroadInfos1 = ArrayList()
        liveEntityList1 = ArrayList()
        video_advs1 = HashMap()
    }

    fun clearCache() {
        video_advs1!!.clear()
    }
    // ========================封装主界面的数据=====================================


    fun getTvAbroadInfos(): List<ItemTv> {
        return tvAbroadInfos1!!
    }

    fun setTvAbroadInfos(tvAbroadInfos: List<ItemTv>) {
        this.tvAbroadInfos1 = tvAbroadInfos
    }

    fun getList_hair(): List<VideoEntity.DataBean> {
        return list_hair1!!
    }

    fun setList_hair(list_hair: List<VideoEntity.DataBean>) {
        this.list_hair1 = list_hair
    }

    fun getList_finger(): List<VideoEntity.DataBean> {
        return list_finger1!!
    }

    fun setList_finger(list_finger: List<VideoEntity.DataBean>) {
        this.list_finger1 = list_finger
    }

    fun getList_body(): List<VideoEntity.DataBean> {
        return list_body1!!
    }

    fun setList_body(list_body: List<VideoEntity.DataBean>) {
        this.list_body1 = list_body
    }

    fun getList_anim(): List<VideoEntity.DataBean> {
        return list_anim1!!
    }

    fun setList_anim(list_anim: List<VideoEntity.DataBean>) {
        this.list_anim1 = list_anim
    }

    fun setVideo_advs(video_advs2: HashMap<String, String>) {
        this.video_advs1 = video_advs2
    }

    fun getVideo_advs(): Map<String, String> {
        return video_advs1!!
    }

    fun saveAdvList() {
        val file = File(AppInfo.VIDEO_ADV_SAVE_DIR)
        if (!file.exists() && file.isDirectory()) {
            file.mkdirs()
        }
        val advs = HashMap<String, String>()
        val files = file.listFiles()
        if (files == null || files!!.size == 0)
            return
        for (file1 in files!!) {
            if (file1.getAbsolutePath().endsWith(".mp4")) {
                val key = file1.getName().replace(".mp4", "").trim({ it <= ' ' })
                advs.put(key, file1.getAbsolutePath())
            }
        }
        MirrorApplication.getInstance().setVideo_advs(advs)
    }

    // =====================SharedPerference读取数据=====================================

    fun saveData(key: String, data: Any) {
        val editor = mSharedPreferences!!.edit()
        try {
            Log.i("SharedPreferences", "设置的tag =$key   //date = $data")
            if (data is Int) {
                editor.putInt(key, data)
            } else if (data is Boolean) {
                editor.putBoolean(key, data)
            } else if (data is String) {
                editor.putString(key, data)
            } else if (data is Float) {
                editor.putFloat(key, data)
            } else if (data is Long) {
                editor.putLong(key, data)
            }
        } catch (e: Exception) {
            Log.i("SharedPreferences", "获取的的tag =" + key + "   //date = " + e.toString())
        }

        editor.commit()
    }

    fun getData(key: String, defaultObject: Any): Any? {
        try {
            Log.i("SharedPreferences", "获取的的tag =" + key + "   //date = " + defaultObject.toString())
            if (defaultObject is String) {
                return mSharedPreferences!!.getString(key, defaultObject)
            } else if (defaultObject is Int) {
                return mSharedPreferences!!.getInt(key, defaultObject)
            } else if (defaultObject is Boolean) {
                return mSharedPreferences!!.getBoolean(key, defaultObject)
            } else if (defaultObject is Float) {
                return mSharedPreferences!!.getFloat(key, defaultObject)
            } else if (defaultObject is Long) {
                return mSharedPreferences!!.getLong(key, defaultObject)
            }
        } catch (e: Exception) {
            Log.i("SharedPreferences", "获取的的tag =" + key + "   //date = " + e.toString())
            return null
        }

        return null
    }
}