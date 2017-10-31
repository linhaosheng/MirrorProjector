package com.mirroproject.config

import android.os.Environment

/**
 * Created by reeman on 2017/10/30.
 */
class AppInfo{

    companion object {
        val DEFAULT_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/interface/tvadinfo.txt"
        val PLAY_URL = "http://cdn.magicmirrormedia.cn/video/13cf787e42ad7e9f1f47de898d8ce7a1.mp4"
        val PLAY_ADV = "http://cdn.magicmirrormedia.cn/video/d86ae6c331dbe71c34360d6eae748dfc.mp4"

        //    ftp访问基本地址
        val FTP_BASE_URL = "http://api.magicmirrormedia.cn/mirr/"

        var EXIT_PWD = "786496"
        var CLEAN_PWD = "762304"
        var SD_PATH = Environment.getExternalStorageDirectory().path
        var VIDEO_CACHE_PATH = SD_PATH + "/MirrorClient/cache/video-cache/"
        var AD_CACHE_PATH = SD_PATH + "/MirrorClient/cache/ad-cache/"
        var SS_CACHE_PATH = SD_PATH + "/MirrorClient/cache/ss-cache/"


        val YY_NAME_APP = "YY直播"
        val YY_PACKAGE = "com.duowan.mobile"
        val YY_ICON = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/appicon/yylive.png"
        val YY_DOWNURL = "http://cdn.magicmirrormedia.cn/mirrorprojector//apkdown/yylive.apk"

        /***
         * 电视家
         */
        val TV_HOME_NAME = "电视家TV"
        val TV_HOME_PACKAGE = "com.elinkway.tvlive2"
        val TV_HOME_ICON = "http://cdn.magicmirrormedia.cn/mirrorprojector//apkdown/appicon/tvhome.png"
        val TV_HOME_DOWNURL = "http://14.215.100.135/app.znds.com/down/20170824/dsj2.0_2.11.8_dangbei.apk"

        /***
         * 斗鱼直播
         */
        val DOUYU_NAME_APP = "斗鱼直播"
        val DOUYU_PACKAGE = "air.tv.douyu.android"
        val DOUYU_ICON = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/appicon/douyuclient.png"
        val DOUYU_DOWNURL = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/douyuclient.apk"

        /**
         * 蜂蜜tv包名
         */
        val BEE_NAME_APP = "蜂蜜直播"
        val BEE_PACKAGE = "com.fengmizhibo.live"
        val BEE_ICON = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/appicon/fengmi.png"
        val BEE_DOWNURL = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/fengmi.apk"

        /***
         * 乐投action
         */
        val LETOU_PACKAGE = "com.hpplay.happyplay.aw"
        val LETOU_ICON = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/appicon/letou.png"
        val LETOU_DOWN_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/apkdown/letou.apk"


        //数据查询常量
        val QUERY_SUCCESS = 0
        val QUERY_FAILED = QUERY_SUCCESS + 1
        val QUERY_ERROR = QUERY_FAILED + 1
        val QUERY_PARSER_ERROR = QUERY_ERROR + 1
        val QUERY_AD_SUCCESS = QUERY_PARSER_ERROR + 1
        val QUERY_AD_FAILED = QUERY_AD_SUCCESS + 1


        val CODE_HAIR = 33  //         <item>发型打理</item>
        val CODE_FINGER = 4457//        <item>美妆美甲</item>
        val CODE_BODY = 4458//        <item>美体塑身</item>
        val CODE_MOVIE = 25//        <item>网络电影</item>
        val CODE_ANIM = 16//        <item>动漫卡通</item>
        val CODE_DEFAULT = 16//

        val CODE_VIDEO_ADV = 17// 视频广告

        val BASE_LOCAL_URL = Environment.getExternalStorageDirectory().path + "/mirror"
        val BASE_APK_PATH = BASE_LOCAL_URL + "/apk"  //apk下载的路径
        val BASE_MUSIC_PATH = BASE_LOCAL_URL + "/music"  //音乐存储的路径
        val BASE_VIDEO_PATH = BASE_LOCAL_URL + "/video"  //视频存储的路径
        val BASE_SCREEN_IMAGE_PATH = BASE_LOCAL_URL + "/screenpic"  //全屏幕屏保广告
        val BASE_URL = "http://api.magicmirrormedia.cn/mirr/apiv1/"
        private val BASE_RQQUEST_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector"

        //软件本体升级，位置已经迁移，请使用新的地址
        val UPDATE_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/update/updatemorror.txt"

        val APK_UPDATE_DOWN_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/update/MirrorMagic.apk"
        val DOWNLOAD_SAVE_APK = BASE_APK_PATH + "/mirror.apk"

        //手机端apk下载地址
        val MOBILE_APK_DOWN_IRL = "http://cdn.magicmirrormedia.cn/mirrorprojector/mirrorMobile/mirrorMobile.apk"


        //设备二维码图片位置
        val ER_CODE_PATH = "/sdcard/mirror/qr/myQr.jpg"
        /***
         * 手机端下载地址
         */
        val ER_CODE_DOWN_APK = "/sdcard/mirror/qr/downurl.jpg"

        //设备投屏二维码图片位置
        val TOU_PING_ER_CODE_PATH = "/sdcard/mirror/touping/myQr.jpg"

        /**
         * 固件升级下载地址
         */
        val FIREWARE_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/systemupdate/update.txt"
        val FIREWARE_UPDATE_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/systemupdate/update_fireware_info.txt"
        val FIREWARE_DOWNLOAD_SAVE_APK = BASE_APK_PATH + "/update.img"

        /**
         * 视频监控升级
         * 不在Launcher里面进行升级 ，移植到Apk里面去升级
         */
        //设备监控APK下载存放路径
        val VIDEO_MONITOR_DOWNLOAD_SAVE_APK = BASE_APK_PATH + "/videomonitor.apk"
        //视频监控
        val VIDEO_MONITOR_PACKAGE_NAME = "com.mirror.mobile"
        val VIDEO_MONITOR_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/videomonitor/videoMonitor.apk"

        //广告视频保存位置
        val VIDEO_ADV_SAVE_DIR = BASE_LOCAL_URL + "/video_adv/"


        //获取图片广告的地址
        val PIC_ADV_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/interface/gifadv.txt"

        //获取图片广告的地址
        val SCREEN_PIC_ADV_URL = "http://cdn.magicmirrormedia.cn/mirrorprojector/interface/screenadv.txt"

        val BASE_URL_HTTP = "http://cdn.magicmirrormedia.cn/mirrorprojector/"
    }
}