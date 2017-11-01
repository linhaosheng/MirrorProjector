package com.mirroproject.http

/**
 * Created by reeman on 2017/10/31.
 */
interface RequeatListener {
    abstract fun requestSuccess(json: String)

    abstract fun requestFailed(errorDesc: String)
}