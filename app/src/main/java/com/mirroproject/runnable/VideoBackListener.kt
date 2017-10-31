package com.mirroproject.runnable

import com.mirroproject.entity.VideoEntity

/**
 * Created by reeman on 2017/10/31.
 */
interface VideoBackListener {

    fun querySuccess(list_video: List<VideoEntity>)

    fun queryFaled(desc: String)

    fun queryError(desc: String)

    fun parsJsonError(error: String)
}