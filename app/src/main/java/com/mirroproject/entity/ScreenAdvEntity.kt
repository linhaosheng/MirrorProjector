package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/30.
 */
data class ScreenAdvEntity(var desc: String, var time: Int, var data: ArrayList<DataBean>) {

    data class DataBean(var url: String) {}
}