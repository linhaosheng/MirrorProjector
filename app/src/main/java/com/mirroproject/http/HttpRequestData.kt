package com.mirroproject.http

import com.mirroproject.entity.*
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path
import com.mirroproject.entity.LoginEntity
import com.mirroproject.entity.DefaultAdvEntity
import com.mirroproject.entity.ADvideoEntity
import com.mirroproject.entity.VideoEntity
import com.mirroproject.entity.ScreenAdvEntity
import com.mirroproject.entity.PicAdvEntity


/**
 * Created by reeman on 2017/11/3.
 */
interface HttpRequestData {

    @GET("interface/gifadv.txt")
    fun getGif(): Flowable<PicAdvEntity>

    @GET("interface/screenadv.txt")
    fun getScreenAd(): Flowable<ScreenAdvEntity>

    @GET("update/updatemorror.txt")
    fun updateInfo(): Flowable<UpdateInfo>

    @GET("systemupdate/update_fireware_info.txt")
    fun systemUpdateInfo(): Flowable<SystemUpdateInfo>

    @GET("interface/tvadinfo.txt")
    fun getDefaultInfo(): Flowable<DefaultAdvEntity>

    @GET("channel/videolist/subchannel_id/{subchannel_id}/token/{token}/orderby/new/start_pos/1/list_num/200")
    fun getVideoDatas(@Path("subchannel_id") subchannel_id: Int, @Path("token") token: String): Flowable<VideoEntity>

    /**
     * String latterUrl = "adv/list/sub_channel_id/" + subchannel_id + "/position/" + playerAdvPosition
     * + "/adv_id/" + advVideoId + "/token/" + token;
     */

    @GET("adv/list/sub_channel_id/{sub_channel_id}/position/{position}/adv_id/{adv_id}/token/{token}")
    fun getVideoInfo(@Path("sub_channel_id") sub_channel_id: Int, @Path("position") position: String, @Path("adv_id") adv_id: Int, @Path("token") token: String): Flowable<ADvideoEntity>

    @GET("user/login/username/{username}/password/{password}/usertype/shop")
    fun getLoginData(@Path("username") username: String, @Path("password") password: String): Flowable<LoginEntity>

    @GET("user/login/username/{username}/password/{password}/macaddress/{macaddress}/usertype/shop")
    fun getMacAddressData(@Path("username") username: String, @Path("password") password: String, @Path("macaddress") macaddress: String): Flowable<LoginEntity>
}
