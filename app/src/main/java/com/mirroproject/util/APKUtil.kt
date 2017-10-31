package com.mirroproject.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.mirroproject.app.MirrorApplication
import com.mirroproject.view.MyToastView
import java.io.File
import java.io.IOException
import java.util.ArrayList

/**
 * Created by reeman on 2017/10/31.
 */
class APKUtil {
    companion object {
        /**
         * 获取所有进程包名
         *
         * @param context
         * @return
         */
        fun getAllProcess(context: Context): ArrayList<String> {
            val list = ArrayList<String>()
            val activityManager = context
                    .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager
                    .runningAppProcesses
            for (runningApp in appProcesses) {
                list.add(runningApp.processName)
            }
            return list
        }

        /***
         * 返回当前前台运行的app
         *
         * @return
         */
        fun appIsRunForset(context: Context): String {
            val activityManager = context
                    .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager
                    .runningAppProcesses
            return appProcesses[0].processName
        }

        /**
         * 安装APK文件
         */
        fun installApk(context: Context, outpath: String) {
            val apkfile = File(outpath)
            if (!apkfile.exists()) {
                MyToastView.getInstances().Toast(context, "安装文件不存在,请先下载")
                return
            }
            // 通过Intent安装APK文件
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive")
            context.startActivity(i)
        }

        /***
         * 判断APK有没有安装
         * @return
         */
        fun ApkState(context: Context, packageName: String): Boolean {
            var isInstall = false
            try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(
                        packageName, 0)
                if (packageInfo != null) {
                    isInstall = true
                } else {
                    isInstall = false
                }
            } catch (e: Exception) {
                isInstall = false
            }

            return isInstall
        }

        //版本名
        fun getVersionName(context: Context): String {
            return getPackageInfo(context)!!.versionName
        }

        //版本号
        fun getVersionCode(context: Context): Int {
            return getPackageInfo(context)!!.versionCode
        }

        private fun getPackageInfo(context: Context): PackageInfo? {
            var pi: PackageInfo? = null
            try {
                val pm = context.packageManager
                pi = pm.getPackageInfo(context.packageName,
                        PackageManager.GET_CONFIGURATIONS)
                return pi
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return pi
        }

        @Throws(IOException::class, InterruptedException::class)
        fun writeFile(str: String) {

            val file = File("/sys/devices/fb.11/graphics/fb0/pwr_bl")
            file.setExecutable(true)
            file.setReadable(true)//设置可读权限
            file.setWritable(true)//设置可写权限
            if (str == "0") {
                do_exec("busybox echo 0 > /sys/devices/fb.11/graphics/fb0/pwr_bl")
            } else {
                do_exec("busybox echo 1 > /sys/devices/fb.11/graphics/fb0/pwr_bl")
            }
        }

        fun do_exec(cmd: String) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                val su: Process
                su = Runtime.getRuntime().exec("su")
                val str = cmd + "\n" + "exit\n"
                su.outputStream.write(str.toByteArray())

                if (su.waitFor() != 0) {
                    println("cmd=$cmd error!")
                    throw SecurityException()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun openOrClose(cmd: String) {
            try {
                APKUtil.writeFile(cmd)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun isLight(): Boolean {
            val powerManager = MirrorApplication.getInstance().getContext()!!.getSystemService(Context.POWER_SERVICE) as PowerManager
            Log.i("light=====", powerManager.isScreenOn.toString() + "")
            return powerManager.isScreenOn
        }

        fun getCurrentSound(): Int {
            val mAudioManager = MirrorApplication.getInstance().getContext()!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return mAudioManager.getStreamVolume(AudioManager.STREAM_RING)
        }
    }
}