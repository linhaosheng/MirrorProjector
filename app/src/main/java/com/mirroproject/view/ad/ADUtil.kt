package com.mirroproject.view.ad

import android.os.Handler
import android.util.Log
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.HttpRequestListener
import com.mirroproject.util.GsonParse
import com.mirroproject.util.SharedPerManager
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by reeman on 2017/11/3.
 */

object ADUtil {
    internal val TAG = "getAdList======="
    var i = 0

    fun getAdList(handler: Handler) {
        val TAG = "getAdList======="
        val BASE_URL = "http://api.magicmirrormedia.cn/mirr/apiv1/"
        val advPosition = "1,3" // 广告位置编号
        var token = SharedPerManager.getToken()   //在 LoginActivity volleyGetLogin 中获取
        if (token.length < 5) {  //这里不用equal""用长度来判断
            token = "dd8330d507ef17adfabb79aff120db09"
        }
        val latterUrl = "adv/list/position/$advPosition/token/$token"
        val url = BASE_URL + latterUrl
        Log.i(TAG, "url==" + url)
        val httpRequest = HttpRequest()
        getAdvList(httpRequest, url, handler)
        //getPicAdvList(httpRequest);
    }

    fun getAdvList(httpRequest: HttpRequest, url: String, handler: Handler) {
        httpRequest.httpGet(url, object : HttpRequestListener {
           override fun requestSuccess(jsonRequest: String) {
                Log.i(TAG, "requestSuccess==" + jsonRequest)
                try {
                    val jsonObject = JSONObject(jsonRequest)
                    if (jsonObject.getInt("state") == 1) {
                        val adInfo = GsonParse.parseADInfo(jsonRequest)
                        val message = handler.obtainMessage()
                        message.obj = adInfo
                        message.what = 1
                        handler.sendMessage(message)
                    }
                    i = 0
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

           override fun requestFailed(error: String) {
                if (i == 5) {
                    Log.i(TAG, "requestFailed==" + error)
                    val message = handler.obtainMessage()
                    message.obj = error
                    message.what = 2
                    handler.sendMessage(message)
                    i = 0
                } else {
                    i++
                    getAdvList(httpRequest, url, handler)
                }
            }
        })
    }
}
