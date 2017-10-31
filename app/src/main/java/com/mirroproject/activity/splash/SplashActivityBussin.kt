package com.mirroproject.activity.splash

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.PicAdvEntity
import com.mirroproject.entity.ScreenAdvEntity
import com.mirroproject.http.RetrofitFactory
import com.mirroproject.service.MirrorService
import com.mirroproject.util.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */

class SplashActivityBussin {

    var retrofitFactory: RetrofitFactory

    constructor(retrofitFactory: RetrofitFactory) {
        this.retrofitFactory = retrofitFactory
    }

    fun getAdvData() {
        val gif = retrofitFactory.providerHttpRequestData().getGif()
        gif.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(Consumer<PicAdvEntity> { picAdvEntity -> MirrorApplication.getInstance().setPicAdvEntity(picAdvEntity) })
    }

    fun getScreenData() {
        val screenAd = retrofitFactory.providerHttpRequestData().getScreenAd()
        screenAd.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Function<ScreenAdvEntity, Publisher<ScreenAdvEntity.DataBean>> {
                    Flowable.create({ FileUtil.creatDirPathNoExists() }, BackpressureStrategy.ERROR)
                }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(Consumer<ScreenAdvEntity.DataBean> { dataBean ->
            val downUrl = dataBean.url
            MirrorService.getInstance().downFile(true, downUrl, AppInfo.BASE_SCREEN_IMAGE_PATH + "/screen" + System.currentTimeMillis() / 1000 + ".jpg")
        })
    }


    fun generateEr() {
        val incremental = Build.VERSION.INCREMENTAL
        Log.i("incremental======", incremental)
        Log.i("blueId====", CodeUtil.getBlueToothCode())
        val deviIdInfo = "镜善镜美" + CodeUtil.getBlueToothCode()
        QRCodeUtil.createErCode(deviIdInfo, AppInfo.ER_CODE_PATH, 300)
        QRCodeUtil.createErCode(AppInfo.MOBILE_APK_DOWN_IRL, AppInfo.ER_CODE_DOWN_APK, 300)
    }

    fun saveScreenSize(activity: Activity) {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val WIDTH = dm.widthPixels
        val HEIGHT = dm.heightPixels
        //        1366/768
        MyLog.i("main", "====测量的尺寸==$WIDTH/$HEIGHT")
        SharedPerManager.setScreenWidth(WIDTH)
        SharedPerManager.setScreenHeight(HEIGHT)
    }

    //打开监控的service
    fun openMonitor() {
        val intent = Intent()
        intent.action = "com.mirror.mobile.action.START_SERVICE_MONITOR"
        intent.`package` = "com.mirror.mobile"
        MirrorApplication.getInstance().startService(intent)
    }
}
