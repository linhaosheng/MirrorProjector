package com.mirroproject.runnable

/**
 * Created by reeman on 2017/11/1.
 */
interface DownListener {
    /**
     * 开始下载
     */
    fun downStart()

    /***
     * 下载进度，和速度
     * @param progress
     * @param speed
     */
    fun downProgress(progress: Int, speed: Long)

    /***
     * 下载完成
     * @param downUrl
     */
    fun downSuccess(downUrl: String)

    /***
     * 下载失败
     * @param failedDesc
     */
    fun downFailed(failedDesc: String)

}
