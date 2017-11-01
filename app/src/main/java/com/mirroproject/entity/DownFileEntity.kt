package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/31.
 */
class DownFileEntity(var downState: Int, var progress: Int, var isDown: Boolean, var desc: String, var downPath: String, var savePath: String, var downSpeed: Int) {

    override fun toString(): String {
        return "DownFileEntity{" +
                "downState=" + downState +
                ", progress=" + progress +
                ", isDown=" + isDown +
                ", desc='" + desc + '\'' +
                ", downPath='" + downPath + '\'' +
                ", savePath='" + savePath + '\'' +
                '}'
    }

    companion object {
        val DOWN_STATE_START = 0
        val DOWN_STATE_PROGRESS = 1
        val DOWN_STATE_SUCCESS = 2
        val DOWN_STATE_FAIED = 3
        val DOWN_STATE_CACLE = 4
    }
}
