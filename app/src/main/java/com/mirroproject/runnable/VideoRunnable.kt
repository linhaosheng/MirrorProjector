package com.mirroproject.runnable

import android.util.Log
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.VideoEntity
import com.mirroproject.http.VideoInfoRetrofitFactory
import com.mirroproject.util.SharedPerManager
import io.reactivex.schedulers.Schedulers

/**
 * Created by reeman on 2017/10/31.
 */

class VideoRunnable(internal var subchannel_id: Int, var retrofitFactory: VideoInfoRetrofitFactory?) : Runnable {

    //        http://api.magicmirrormedia.cn/mirr/apiv1/channel/videolist/subchannel_id/33/token/dd8330d507ef17adfabb79aff120db09/orderby/new/start_pos/1/list_num/200

    override fun run() {
        val token = SharedPerManager.getToken()
        getDatas(retrofitFactory, subchannel_id, token)
    }

    private fun saveAppList(lists: List<VideoEntity.DataBean>) {
        when (subchannel_id) {
            AppInfo.CODE_HAIR -> MirrorApplication.getInstance().setList_hair(lists)
            AppInfo.CODE_FINGER -> MirrorApplication.getInstance().setList_finger(lists)
            AppInfo.CODE_BODY -> MirrorApplication.getInstance().setList_body(lists)
            AppInfo.CODE_ANIM -> MirrorApplication.getInstance().setList_anim(lists)
        }
    }

    fun getDatas(retrofitFactory: VideoInfoRetrofitFactory?, subchannel_id: Int, token: String) {
        retrofitFactory?.providerHttpRequestData()!!.getVideoDatas(subchannel_id, token)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({ videoEntity ->
                    if (videoEntity != null && videoEntity.data != null) {
                        saveAppList(videoEntity.data)
                    }
                }) { throwable -> Log.e(TAG, "getDatas error===" + throwable.message) }
    }

    companion object {

        val TAG = "MirrorService"
    }
}
