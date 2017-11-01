package com.mirroproject.http

/**
 * Created by reeman on 2017/11/1.
 */
interface HttpRequestListener {

    /***
     * 请求成功
     * @param jsonRequest
     */
    fun requestSuccess(jsonRequest: String)

    /***
     * 请求失败
     * @param error
     */
    fun requestFailed(error: String)
}