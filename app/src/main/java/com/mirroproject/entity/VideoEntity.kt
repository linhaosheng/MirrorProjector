package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/30.
 */
data class VideoEntity(var state: Int, var data: ArrayList<DataBean>) {

    data class DataBean(var id: String, var subchannel_id: String, var title: String, var description: String, var videopath: String, var picpath: String, var time: String, var viewcount: String, var createtime: String) {}
}