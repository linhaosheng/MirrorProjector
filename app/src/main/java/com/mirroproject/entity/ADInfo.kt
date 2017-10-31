package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/31.
 */
data class ADInfo(var state: Int, var data: DataBean) {

    data class DataBean(var pos_1: ArrayList<Pos1Bean>, var pos_3: ArrayList<Pos3Bean>) {}

    data class Pos1Bean(var id: String, var title: String, var type: String, var position: String, var text: String, var href: String, var viewcount: String, var videopath: String, var picpath: String, var schedule_weight: String, var timelength: String) {}

    data class Pos3Bean(var id: String, var title: String, var type: String, var position: String, var text: String, var href: String, var viewcount: String, var videopath: String, var picpath: String, var schedule_weight: String, var timelength: String) {}
}