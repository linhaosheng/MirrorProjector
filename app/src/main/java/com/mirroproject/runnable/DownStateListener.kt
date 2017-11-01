package com.mirroproject.runnable

import com.mirroproject.entity.DownFileEntity

/**
 * Created by reeman on 2017/10/31.
 */
/**
 * 下载状态回调
 */

interface DownStateListener {

    fun downStateInfo(entity: DownFileEntity)
}
