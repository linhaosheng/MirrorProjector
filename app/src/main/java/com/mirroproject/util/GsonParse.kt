package com.mirroproject.util

import android.util.Log
import com.google.gson.Gson
import com.mirroproject.app.MirrorApplication
import com.mirroproject.entity.*
import com.mirroproject.http.ADVideoListener
import com.mirroproject.http.LoginStateListener
import com.mirroproject.http.TvDataLintener
import com.mirroproject.runnable.VideoBackListener
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by reeman on 2017/10/31.
 */
class GsonParse {
    companion object {
        lateinit var instance1: Gson

        fun getInstance(): Gson {
            if (instance1 == null)
                synchronized(GsonParse::class) {
                    if (instance1 == null) {
                        instance1 = Gson()
                    }
                }
            return instance1
        }

        fun parseDefaultAdv(json: String): DefaultAdvEntity? {
            val gson = Gson()
            return gson.fromJson<DefaultAdvEntity>(json, DefaultAdvEntity::class.java)
        }

        fun updatePicAdv(updateJson: String): PicAdvEntity? {
            val gson = Gson()
            return gson.fromJson<PicAdvEntity>(updateJson, PicAdvEntity::class.java)
        }

        fun updateScreenPicAdv(updateJson: String): ScreenAdvEntity? {
            val gson = Gson()
            return gson.fromJson<ScreenAdvEntity>(updateJson, ScreenAdvEntity::class.java)
        }

        fun updateParse(updateJson: String): UpdateInfo? {
            val gson = Gson()
            return gson.fromJson<UpdateInfo>(updateJson, UpdateInfo::class.java)
        }

        fun updateSystemParse(updateJson: String): SystemUpdateInfo? {
            val gson = Gson()
            return gson.fromJson<SystemUpdateInfo>(updateJson, SystemUpdateInfo::class.java)
        }

        val TAG = "MirrorService"


        private fun parserFailed(json: String, listener: ADVideoListener) {
            try {
                val `object` = JSONObject(json)
                val code = `object`.getInt("code")
                val error_text = `object`.getString("error_text")
                listener.requestFailed("错误代码: $code\n错误信息: $error_text")
            } catch (e: Exception) {
                listener.requestFailed(e.toString())
            }

        }

        fun parserLogin(json: String, listener: LoginStateListener) {
            try {
                val `object` = JSONObject(json)
                val state = `object`.getInt("state")
                if (state == 1) {  //登录成功
                    val data = `object`.getString("data")
                    Log.i("login", "data==" + data)
                    parseSuccess(data, listener)
                } else {
                    parserFailed(json, listener)
                }
            } catch (e: Exception) {
                listener.parsenError(e.toString())
            }

        }


        private fun parserFailed(json: String, listener: LoginStateListener) {
            try {
                val `object` = JSONObject(json)
                val code = `object`.getInt("code")
                val error_text = `object`.getString("error_text")
                listener.loginFailed("错误代码: $code\n错误信息: $error_text")
            } catch (e: Exception) {
                listener.parsenError(e.toString())
                Log.i("loginFail", "====3" + e.message)
            }

        }

        private fun parseSuccess(data: String, listener: LoginStateListener) {
            try {
                val `object` = JSONObject(data)
                val id = `object`.getInt("id")
                val name = `object`.getString("name")
                val username = `object`.getString("username")
                val password = `object`.getString("password")
                val size = `object`.getInt("size")
                val station_num = `object`.getString("station_num")
                val npd = `object`.getInt("npd")
                val contact = `object`.getString("contact")
                val contact_mobile = `object`.getInt("contact_mobile")
                val tel = `object`.getInt("tel")
                val house_type = `object`.getInt("house_type")
                val rent_cutoff_data = `object`.getString("rent_cutoff_data")
                val province_name = `object`.getString("province_name")
                val city_name = `object`.getString("city_name")
                val area_name = `object`.getString("area_name")
                val address = `object`.getString("address")
                val token = `object`.getString("token")
                val expire_time = `object`.getString("expire_time")
                SharedPerManager.setLoginInfo(data)
                SharedPerManager.setToken(token)
                listener.loginSuccess()
            } catch (e: Exception) {
                Log.i("loginFail", "====4" + e.message)
                listener.parsenError(e.toString())
            }

        }

        // 解析电视直播
        fun parserTvInfo(json: String, listener: TvDataLintener): ArrayList<ItemTv> {
            val lists = ArrayList<ItemTv>()
            try {
                val jsonObject = JSONObject(json)
                val jsonArray = jsonObject.getJSONArray("channellist")
                for (i in 0 until jsonArray.length()) {
                    val jsonObjectSon = jsonArray.opt(i) as JSONObject
                    val id = jsonObjectSon.getString("id")
                    val name = jsonObjectSon.getString("name")
                    val pic = jsonObjectSon.getString("pic")
                    val packName = jsonObjectSon.getString("package")
                    if (name.contains("中央")
                            || name.toLowerCase().contains("cctv")
                            || name.toLowerCase().contains("btv")
                            || name.contains("卫视")
                            || name.contains("中国")
                            || name.contains("教育")
                            || name.contains("财经")
                            || name.contains("电视")
                            || name.contains("体育")
                            || name.contains("卡通")
                            || name.contains("游戏")
                            || name.contains("少儿")
                            || name.contains("湖南")
                            || name.contains("电影")) {
                        if (pic.length > 3) {
                            val itemTv = ItemTv(id, name, pic, packName)
                            MyLog.i("MirrorService", "电视直播数据==" + itemTv.toString())
                            lists.add(itemTv)
                        }
                    }
                }
                MirrorApplication.getInstance().setTvAbroadInfos(lists)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            listener.queryTvSuccess(lists)
            return lists
        }

        fun parserVideo(jsonDesc: String, listener: VideoBackListener) {
        }

        //解析获取的广告
        fun parseADInfo(json: String): ADInfo? {
            val gson = Gson()
            return gson.fromJson<ADInfo>(json, ADInfo::class.java)
        }
    }
}