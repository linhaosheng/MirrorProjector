package com.mirroproject.http

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by reeman on 2017/10/31.
 */
class MyOkHttpUtil(internal var requestUrl: String) {
    internal var mOkHttpClient: OkHttpClient? = null

    fun setRequestUrl(url: String) {
        this.requestUrl = url
    }

    fun getRequestInfo(listener: RequeatListener) {
        Log.e(TAG, "===url:" + requestUrl)
        try {
            if (mOkHttpClient == null) {
                mOkHttpClient = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()
            }
            val request = Request.Builder().url(requestUrl).build()
            val call = mOkHttpClient!!.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(arg0: Call, arg1: IOException) {
                    Log.i(TAG, "===网络请求失败==" + arg1.toString())
                    listener.requestFailed(arg1.toString())
                }

                @Throws(IOException::class)
                override fun onResponse(arg0: Call, response: Response) {
                    val json = response.body()!!.string()
                    listener.requestSuccess(json)
                }
            })
        } catch (e: Exception) {
            Log.i(TAG, "===网络请求异常==" + e.toString())
            listener.requestFailed(e.toString())
        }

    }

    companion object {


        val TAG = "MyOkHttpUtil"
    }


}
