package com.mirroproject.http

import android.util.Log
import com.mirroproject.config.AppInfo
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by reeman on 2017/11/1.
 */
class HttpRequest {
    var mOkHttpClient: OkHttpClient? = null

    /***
     * GET请求
     *
     * @param url
     * @param listener
     */
    fun httpGet(url: String, listener: HttpRequestListener) {
        if (mOkHttpClient == null) {
            mOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
        }
        Log.i("url=========", "httpGet" + url)
        val request = Request.Builder().url(url).build()
        val call = mOkHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(arg0: Call, arg1: IOException) {
                Log.i("http", "====网络请求失败===" + arg1.toString())
                listener.requestFailed(arg1.message!!)
            }

            @Throws(IOException::class)
            override fun onResponse(arg0: Call, response: Response) {
                val htmlStr = response.body()!!.string()
                Log.i("http", "====网络请求成功===" + htmlStr)
                listener.requestSuccess(htmlStr)
            }
        })
    }

    //统计广告播放次数
    fun advPlayCount(token: String,
                     videoId: Int, adId: Int, isInsertAd: Boolean, listener: HttpRequestListener) {
        var url: String
        if (isInsertAd) {
            url = "adv/viewcount/token/$token/ad_id/$videoId"
        } else {
            url = "adv/viewcount/token/$token/ad_id/$adId/video_id/$videoId"
        }
        url = AppInfo.BASE_URL + url
        Log.i("advPlayCount======", url)
        if (mOkHttpClient == null) {
            mOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .connectionPool(ConnectionPool(10, 20, TimeUnit.SECONDS))
                    .build()
        }

        val request = Request.Builder().url(url).tag("advPlayCount").build()
        mOkHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()!!.string()
                try {
                    val jsonObject = JSONObject(json)
                    val state = jsonObject.getInt("state")
                    //广告统计次数错误
                    if (state == 0) {
                        val error_msg = jsonObject.getString("error_msg")
                        listener.requestSuccess(error_msg)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

    //统计广告次数
    fun AdvClickCount(token: String, videoId: String, adId: String, listener: HttpRequestListener) {
        val BASE_URL = "http://api.magicmirrormedia.cn/mirr/apiv1/adv/click/"
        val url = BASE_URL + "token/" + token + "/ad_id/" + adId + "/video_id/" + videoId
        Log.i("url=====", url)
        if (mOkHttpClient == null) {
            mOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .connectionPool(ConnectionPool(10, 20, TimeUnit.SECONDS))
                    .build()
        }
        val request = Request.Builder().url(url).build()
        mOkHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.requestFailed(e.message!!)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val string = response.body()!!.string()
                Log.i("AdvClick====", string)
            }
        })
    }
}