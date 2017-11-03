package com.mirroproject.http

/**
 * Created by reeman on 2017/10/31.
 */
interface RequeatListener {
     fun requestSuccess(json: String)
     fun requestFailed(errorDesc: String)
}