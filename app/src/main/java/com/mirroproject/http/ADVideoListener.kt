package com.mirroproject.http

import com.mirroproject.entity.ADvideoEntity

/**
 * Created by reeman on 2017/10/31.
 */
interface ADVideoListener {

    fun requestSuccess(lists: List<ADvideoEntity>)

    fun requestFailed(error: String)

}
