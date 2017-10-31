package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/30.
 */
data class ADvideoEntity(var state: Int, var data: DataBean) {

    data class DataBean(var pos_8: ArrayList<Any>, var pos_9: ArrayList<Any>, var pos_10: ArrayList<Any>, var pos_11: ArrayList<Pos11Bean>) {}

    data class Pos11Bean(var id: String, var title: String, var type: String, var position: String, var text: String, var viewcount: String, var videopath: String, var picpath: Any, var timelength: String, var clickable: String) {}
}