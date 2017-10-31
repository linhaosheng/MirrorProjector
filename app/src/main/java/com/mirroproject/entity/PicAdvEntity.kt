package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/30.
 */
data class PicAdvEntity(var desc: String, var time: Int, var everyTime: Int, var data: ArrayList<DataBean>) {

    data class DataBean(var url: String, var isGif: Boolean) {
    }
}