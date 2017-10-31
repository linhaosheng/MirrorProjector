package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/30.
 */
data class LoginEntity(var state: Int, var data: DataBean) {

    data class DataBean(var id: String, var name: String, var username: String, var password: String, var size: String, var station_num: String, var npd: String, var contact: String, var contact_mobile: String, var tel: String, var babershop_type: String, var house_type: String, var rent_cutoff_data: String, var wifi: String, var province_name: String, var city_name: String, var area_name: String, var address: String, var lng: String, var lat: String, var mirrtype_guaqiang: String, var mirrtype_luodijing: String, var mirrtype_qiangmianjing: String, var mirrtype_danmianjing: String, var mirrtype_shuangmianjing: String, var join_type: String, var status: String, var comment: String, var market_uid: String, var pic1: String, var pic2: String, var pic3: String, var pic4: String, var pic5: Object, var pic6: Object, var pic7: Object, var pic8: Object, var service: String, var bandwidth: String, var net_status: String, var contract_type: String, var createdate: String, var updatedate: String, var updateby: String, var watch_dog: String, var last_login_time: String, var token: String, var expire_time: String) {}
}