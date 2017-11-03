package com.mirroproject.runnable

import android.os.Handler
import android.util.Log
import com.lidroid.xutils.HttpUtils
import com.lidroid.xutils.exception.HttpException
import com.lidroid.xutils.http.HttpHandler
import com.lidroid.xutils.http.ResponseInfo
import com.lidroid.xutils.http.callback.RequestCallBack
import com.mirroproject.entity.DownFileEntity
import java.io.File

/**
 * Created by reeman on 2017/10/31.
 */

class DownRunnable(var downUrl: String, var saveUrl: String, var listener: DownStateListener) : Runnable {

    lateinit var httHhandler: HttpHandler<File>
    lateinit var httpUtils: HttpUtils
    var downSum: Long = 0
    var isFalse = false

    private val handler = Handler()

    fun setIsDelFile(isDelFile: Boolean) {
        this.isFalse = isDelFile
    }

    override fun run() {
        //第一个参数:下载地址
        //第二个参数:文件存储路径
        //第三个参数:是否断点续传
        //第四个参数:是否重命名
        //第五个参数:请求回调
        try {
            if (httpUtils == null) {
                httpUtils = HttpUtils()
            }
            val fileDir = File(saveUrl)
            //如果不需要断点续传，这里可以删除
            if (isFalse) {
                if (fileDir.exists()) {
                    Log.i("down", "======文件存在，删除文件")
                    fileDir.delete()
                }
            }
            if (!fileDir.exists()) {
                fileDir.createNewFile()
            }
            httpUtils!!.configRequestThreadPoolSize(5)//设置由几条线程进行下载
            httHhandler = httpUtils!!.download(downUrl, saveUrl, true, true, object : RequestCallBack<File>() {
                override fun onStart() {
                    super.onStart()
                    Log.e("down", "======开始下载")
                    downSum = 0
                    backState("开始下载", DownFileEntity.DOWN_STATE_START, 0, true, downUrl, saveUrl, 1000)
                }

                override fun onLoading(total: Long, current: Long, isUploading: Boolean) {
                    super.onLoading(total, current, isUploading)
                    val progress = (current * 100 / total).toInt()
                    val speed = ((current - downSum) / 1024).toInt()
                    Log.e("down", "======下载进度==progress=$progress   current=$current/$total    /speed = $speed")
                    backState("下载中", DownFileEntity.DOWN_STATE_PROGRESS, progress, true, downUrl, saveUrl, speed)
                    downSum = current
                }


                override fun onSuccess(responseInfo: ResponseInfo<File>) {
                    Log.e("down", "===下载成功")
                    backState("下载成功", DownFileEntity.DOWN_STATE_SUCCESS, 100, false, downUrl, saveUrl, 0)
                }

                override fun onFailure(e: HttpException, s: String) {
                    Log.e("down", "===下载失败==" + s + "   /" + e.toString())
                    backState(s, DownFileEntity.DOWN_STATE_FAIED, 0, false, downUrl, saveUrl, -1)
                }
            })
        } catch (e: Exception) {
            Log.i("down", "下载异常==" + e.toString())
            backState(e.toString(), DownFileEntity.DOWN_STATE_FAIED, 0, false, downUrl, saveUrl, -1)
        }

    }

    fun stopDown() {
        backState("未获取到文件，请点击重试!", DownFileEntity.DOWN_STATE_CACLE, 0, false, downUrl, saveUrl, -1)
        try {
            if (httHhandler == null) {
                return
            }
            httHhandler!!.cancel()
        } catch (e: Exception) {
            Log.e("down", "停止下载异常==" + e.toString())
        }

    }

     fun backState(state: String, downState: Int, progress: Int, b: Boolean, downUrl: String, saveUrl: String, speed: Int) {
        val entity = DownFileEntity(downState, progress, b, state, downUrl, saveUrl, speed)
        listener.downStateInfo(entity)
    }
}
