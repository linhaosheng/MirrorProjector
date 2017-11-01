package com.mirroproject.util

import android.util.Log
import com.mirroproject.runnable.DownListener
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by reeman on 2017/11/1.
 */
class DownUtil(internal var downListener: DownListener) : Callback {

    var mOkHttpClient: OkHttpClient
    lateinit var downPath: String
    lateinit var downUrl: String

    internal var lastProgress = 0

    init {
        mOkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
    }


    fun downFile(downUrl: String, downPath: String) {
        this.downUrl = downUrl
        this.downPath = downPath
        try {
            val file = File(downPath)
            if (file.exists()) {
                file.delete()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            downListener.downStart()
            val downDir = downPath.substring(0, downPath.lastIndexOf("/")).trim { it <= ' ' }
            val downName = downPath.substring(downPath.lastIndexOf("/") + 1, downPath.length).trim { it <= ' ' }
            Log.i("down", "下载地址==$downDir   下载的名字==$downName")
            val destDir = File(downDir)
            if (destDir.isDirectory && !destDir.exists()) {
                destDir.mkdirs()
            }
            val request = Request.Builder().url(downUrl).build()
            mOkHttpClient.newCall(request).enqueue(this)
        } catch (e: Exception) {
            downListener.downFailed(e.toString())
            Log.d("down", "=================error==" + e.toString())
        }

    }

    fun cacleDown() {
        val dispatcher = mOkHttpClient.dispatcher()
        for (call in dispatcher.runningCalls()) {
            call.cancel()
        }
    }


    override fun onFailure(call: Call, e: IOException) {
        downListener.downFailed(e.toString())
        Log.d("down", "onFailure")
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        var inputStream: InputStream? = null
        val buf = ByteArray(2048)
        var len = 0
        var fos: FileOutputStream? = null
        try {
            inputStream = response.body()!!.byteStream()
            val total = response.body()!!.contentLength()
            val file = File(downPath)
            if (file.exists()) {
                Log.i("down", "文件存在，删除文件==")
                file.delete()
            }
            if (!file.exists() && file.isFile) {
                Log.i("down", "下载文件不存在创建文件==")
                val isCreat = file.createNewFile()
                Log.i("down", "创建文件==" + isCreat)
            }
            fos = FileOutputStream(file)
            var sum: Long = 0
            var saveSum: Long = 0
            while ({ len = inputStream!!.read(buf);len }() != -1) {
                fos.write(buf, 0, len)
                sum += len.toLong()
                val progress = (sum * 1.0f / total * 100).toInt()
                val speed = sum - saveSum
                saveSum = sum
                Log.d("down", "===================$progress  speed =$speed")
                updateProgress(progress, speed)
            }
            fos.flush()
            downListener.downSuccess(downPath)
            Log.d("down", "=================success==")
        } catch (e: Exception) {
            downListener.downFailed(e.toString())
            Log.d("down", "=================error==" + e.toString())
        } finally {
            if (inputStream != null)
                inputStream.close()
            if (fos != null)
                fos.close()
        }
    }

    private fun updateProgress(progress: Int, speed: Long) {
        if (progress > lastProgress) {
            downListener.downProgress(progress, speed)
        }
        lastProgress = progress
    }
}
