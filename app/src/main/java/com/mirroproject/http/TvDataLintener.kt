package com.mirroproject.http

import com.mirroproject.entity.ItemTv

/**
 * Created by reeman on 2017/10/31.
 */
/***
 * 直播数据返回接口
 */
interface TvDataLintener {

    fun queryTvSuccess(tvAbroadInfos: List<ItemTv>)

    fun queryFailed(desc: String)
}