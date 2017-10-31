package com.mirroproject.util

import android.util.Log

/**
 * Created by reeman on 2017/10/31.
 */
object MyLog {

    fun i(tag: String, desc: String) {
        Log.i(tag, desc)
    }

    fun e(tag: String, desc: String) {
        Log.e(tag, desc)
    }

}
