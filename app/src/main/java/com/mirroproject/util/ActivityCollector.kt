package com.mirroproject.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.util.Log

/**
 * Created by reeman on 2017/10/30.
 */
class ActivityCollector {

    companion object {
        var activites: ArrayList<Activity> = ArrayList()

        fun isForeground(context: Context, classNmae: String): Boolean {
            if (context == null || TextUtils.isEmpty(classNmae)) {
                return false
            }
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(1)
            if (list != null && list.size > 0) {
                val cpn = list[0].topActivity
                Log.i("activityName---", cpn.className)
                if (classNmae == cpn.className)
                    return true
            }
            return false
        }

        fun addActivity(activity: Activity) {
            activites.add(activity)
        }

        fun removeActivity(activity: Activity) {
            activites.remove(activity)
        }

        fun finishAll() {
            for (activity in activites) {
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
        }
    }

}