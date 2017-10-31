package com.mirroproject.http;

import com.mirroproject.entity.ADvideoEntity;
import com.mirroproject.entity.DefaultAdvEntity;
import com.mirroproject.entity.LoginEntity;
import com.mirroproject.entity.PicAdvEntity;
import com.mirroproject.entity.ScreenAdvEntity;
import com.mirroproject.entity.SystemUpdateInfo;
import com.mirroproject.entity.UpdateInfo;
import com.mirroproject.entity.VideoEntity;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by reeman on 2017/10/30.
 */

 public interface HttpRequestData {

    @GET("interface/gifadv.txt")
    Flowable<PicAdvEntity> getGif();

    @GET("interface/screenadv.txt")
    Flowable<ScreenAdvEntity> getScreenAd();

    @GET("update/updatemorror.txt")
    Flowable<UpdateInfo> getUpdateInfo();

    @GET("systemupdate/update_fireware_info.txt")
    Flowable<SystemUpdateInfo> getSystemUpdateInfo();

    @GET("channel/videolist/subchannel_id/{subchannel_id}/token/{token}/orderby/new/start_pos/1/list_num/200")
    Flowable<VideoEntity> getVideoDatas(@Path("subchannel_id") int subchannel_id, @Path("token") String token);

    /**
     * String latterUrl = "adv/list/sub_channel_id/" + subchannel_id + "/position/" + playerAdvPosition
     * + "/adv_id/" + advVideoId + "/token/" + token;
     */

    @GET("adv/list/sub_channel_id/{sub_channel_id}/position/{position}/adv_id/{adv_id}/token/{token}")
    Flowable<ADvideoEntity> getVideoInfo(@Path("sub_channel_id") int sub_channel_id, @Path("position") String position, @Path("adv_id") int adv_id, @Path("token") String token);

    @GET("interface/tvadinfo.txt")
    Flowable<DefaultAdvEntity> getDefaultInfo();

    @GET("user/login/username/{username}/password/{password}/usertype/shop")
    Flowable<LoginEntity> getLoginData(@Path("username") String username, @Path("password") String password);

    @GET("user/login/username/{username}/password/{password}/macaddress/{macaddress}/usertype/shop")
    Flowable<LoginEntity> getMacAddressData(@Path("username") String username, @Path("password") String password, @Path("macaddress") String macaddress);
}
