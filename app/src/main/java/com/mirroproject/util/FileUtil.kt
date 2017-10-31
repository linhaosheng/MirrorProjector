package com.mirroproject.util

import android.content.Context
import com.mirroproject.R
import com.mirroproject.config.AppInfo
import java.io.*

/**
 * Created by reeman on 2017/10/31.
 */
class FileUtil {
    internal var context: Context? = null

    fun FileUtil(context: Context) {
        this.context = context
    }

    companion object {
        val TAG = "FileUtil"
        fun creatDirPathNoExists() {
            try {
                val baseFile = File(AppInfo.BASE_LOCAL_URL)  // /sdcard/mirror
                if (!baseFile.exists()) {
                    val isCreat = baseFile.mkdirs()
                    MyLog.i(TAG, "====创建文件夹返回值 ===" + isCreat)
                }
                val file = File(AppInfo.BASE_VIDEO_PATH)    // /sdcard/mirror/video
                if (!file.exists()) {
                    val isFilr = file.mkdirs()
                    MyLog.i(TAG, "====创建文件返回值 ===" + isFilr)
                }
                val music = File(AppInfo.BASE_MUSIC_PATH)    // /sdcard/mirror/music
                if (!music.exists()) {
                    val isFilr = music.mkdirs()
                    MyLog.i(TAG, "====创建文件返回值 ===" + isFilr)
                }
                val apkdown = File(AppInfo.BASE_APK_PATH)     // /sdcard/mirror/apk
                if (!apkdown.exists()) {
                    val isFilr = apkdown.mkdirs()
                    MyLog.i(TAG, "====创建文件返回值 ===" + isFilr)
                }
                val fileDir = File(AppInfo.BASE_APK_PATH + "/file")    // /sdcard/mirror/apk/file
                if (!fileDir.exists()) {
                    val isFilr = apkdown.mkdirs()
                    MyLog.i(TAG, "====创建文件返回值 ===" + isFilr)
                }
                val fileScreen = File(AppInfo.BASE_SCREEN_IMAGE_PATH)    // /sdcard/mirror/screenpic
                if (!fileScreen.exists()) {
                    val isFilr = fileScreen.mkdirs()
                    MyLog.i(TAG, "====创建文件返回值 ===" + isFilr)
                }
            } catch (e: Exception) {
                MyLog.i(TAG, "====创建异常 ===" + e.toString())
            }

        }

        //删除目录或者文件
        fun deleteDirOrFile(Path: String): Boolean {
            return deleteDirOrFile(File(Path))
        }

        fun deleteDirOrFile(dir: File): Boolean {
            if (!dir.exists()) {
                return true
            }
            if (dir.isDirectory) {
                val children = dir.list()
                if (children == null || children.size == 0) {
                    return true
                }
                for (i in children.indices) {
                    val success = deleteDirOrFile(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
            return dir.delete()
        }

        fun writeLine(fileName: String, content: String) {
            var fileName = fileName
            try {
                fileName = AppInfo.BASE_LOCAL_URL + "/file/1.txt"
                val file = File(fileName)
                val parentFile = file.parentFile
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                val writer = FileWriter(fileName, true)
                writer.write(content)
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}