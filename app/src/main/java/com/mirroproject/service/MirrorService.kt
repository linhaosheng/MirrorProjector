package com.mirroproject.service

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.mirroproject.activity.LauncherActivity
import com.mirroproject.activity.LoginActivity
import com.mirroproject.activity.main.MainActivity
import com.mirroproject.activity.splash.SplashActivity
import com.mirroproject.activity.video.VideoPlayActivity
import com.mirroproject.adapter.VideoViewAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.app.DaggerAppComponent
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.*
import com.mirroproject.http.*
import com.mirroproject.runnable.*
import com.mirroproject.runnable.TvDataLintener
import com.mirroproject.util.*
import com.mirroproject.view.MyToastView
import com.mirroproject.view.WaitDialogUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */
class MirrorService : Service(), VideoBackListener, TvDataLintener {
    val TAG = "MirrorService"
    var executor = Executors.newFixedThreadPool(10)
    val KEYCODE_HOME = "com.ys.keyevent_home"
    val KEYCODE_OPEN = "com.ys.keyevent_power"
    val BOOT_OPEN = "android.intent.action.BOOT_COMPLETED"
    val LOGIN_IM_SUCCESS_TYPE = "login_im_success_type"
    val VOLUEM_CHANGE = "android.media.VOLUME_CHANGED_ACTION"
    var isLight = true
    var isLoadFireware = false
    var loginImSuccess = false
    private val SCREEN_ADV = 0x15

    @Inject
    lateinit var retrofitFactory: RetrofitFactory
    @Inject
    lateinit var videoInfoRetrofitFactory: VideoInfoRetrofitFactory

    companion object {
        lateinit var instance1: MirrorService
        fun getInstance(): MirrorService {
            if (instance1 == null) {
                synchronized(MirrorService::class.java) {
                    if (instance1 == null) {
                        instance1 = MirrorService()
                    }
                }
            }
            return instance1
        }
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == KEYCODE_HOME) {
                Log.i("MAIN", "====用户点击了home按钮")
                if (ActivityCollector.isForeground(this@MirrorService, MainActivity::class.java!!.getName())) {
                    return
                } else if (ActivityCollector.isForeground(this@MirrorService, SplashActivity::class.java!!.getName())) {
                    return
                } else if (ActivityCollector.isForeground(this@MirrorService, LoginActivity::class.java!!.getName())) {
                    return
                } else if (!SharedPerManager.isLogin()) {
                    if (ActivityCollector.isForeground(this@MirrorService, LauncherActivity::class.java!!.getName())) {
                        return
                    }
                }
                val intentActivity = Intent()
                intentActivity.setClass(this@MirrorService, MainActivity::class.java!!)
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentActivity)
            } else if (action == KEYCODE_OPEN) {
                if (isLight) {
                    APKUtil.openOrClose("0")
                    isLight = false
                } else {
                    APKUtil.openOrClose("1")
                    isLight = true
                }
                if (ActivityCollector.isForeground(this@MirrorService, VideoPlayActivity::class.java!!.getName())) {
                    EventBus.getDefault().post(EventType.CloseScreenType(1))
                }
            } else if (action == BOOT_OPEN) {
                APKUtil.openOrClose("1")
                isLight = true
            } else if (action == LOGIN_IM_SUCCESS_TYPE) {
                loginImSuccess = true
            } else if (action == VOLUEM_CHANGE) {
                //暂时去掉次功能
                //                setVoluem();
            }
        }
    }

    lateinit var mAudioManager: AudioManager

    private fun setVoluem() {
        if (mAudioManager == null) {
            mAudioManager = getSystemService(Service.AUDIO_SERVICE) as AudioManager
        }
        val max = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val current = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val setCurrentVoluem = SharedPerManager.getMaxVolumn()
        Log.i("voluem", "=====音量设定==$current/$max  setCurrentVoluem====$setCurrentVoluem")
        if (current > setCurrentVoluem) {
            mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, setCurrentVoluem, 0)
            MyToastView.getInstances().Toast(this@MirrorService, "声音太大会影响到其他的客户哦！")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance1 = this
        Log.i(TAG, "======启动服务MirrorService")
        DaggerAppComponent.builder().appModule(AppModule(MirrorApplication.getInstance())).build().inject(this)
        getMainInfo()
        initReceiver()
    }

    private val PROJECT_AP_TIME = SharedPerManager.getScreenAdTime()

    //=======================================================================================================
    fun autoLogin() {
        try {
            val login_username = SharedPerManager.getUserName()
            val login_password = SharedPerManager.getPassword()
            if (TextUtils.isEmpty(login_username) || TextUtils.isEmpty(login_password)) {
                return
            }
            val macAddress = CodeUtil.getBlueToothCode()
            if (macAddress.length < 5) {
                videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getLoginData(login_username, login_password)
                        .compose(RxJavaHelp.setThread())
                        .subscribe({ loginEntity -> EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_SUCCESS)) }, { throwable -> EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_ERROR)) })
            } else {
                videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getMacAddressData(login_username, login_password, macAddress)
                        .compose(RxJavaHelp.setThread())
                        .subscribe({ loginEntity ->
                            if (loginEntity.state !== 1) {
                                EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_ERROR))
                            } else {
                                EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_SUCCESS))
                                SharedPerManager.setToken(loginEntity.data.token)
                            }
                        }, { throwable ->
                            throwable.printStackTrace()
                            Log.i(TAG, "autoLogin fail====" + throwable.message)
                            EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_ERROR))
                        })
            }
        } catch (e: Exception) {
            EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_ERROR))
            e.printStackTrace()
        }

    }

    fun login(waitDialogUtil: WaitDialogUtil, activity: Activity, login_username: String, login_password: String) {
        SharedPerManager.setUserName(login_username)
        SharedPerManager.setPassword(login_password)
        val macAddress = CodeUtil.getBlueToothCode()
        if (macAddress.length < 5) {
            videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getLoginData(login_username, login_password)
                    .compose(RxJavaHelp.setThreadWithDialog(waitDialogUtil, "登录中..."))
                    .subscribe({ loginEntity -> EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_SUCCESS)) },
                            { throwable ->
                                throwable.printStackTrace()
                                Log.i(TAG, "login fail====" + throwable.message)
                                EventBus.getDefault().post(EventType.LoginType(EventType.LoginType.LOGIN_ERROR))
                            })
        } else {
            videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getMacAddressData(login_username, login_password, macAddress)
                    .compose(RxJavaHelp.setThreadWithDialog(waitDialogUtil, "登录中..."))
                    .subscribe({ loginEntity ->
                        if (loginEntity.state !== 1) {
                            MyToastView.getInstances().Toast(MirrorApplication.getInstance(), "登录失败\n")
                        } else {
                            MyToastView.getInstances().Toast(MirrorApplication.getInstance(), "登录成功")
                            startService(Intent(activity, PopupService::class.java))  //GIf悬浮窗界面开始
                            startService(Intent(activity, MirrorService::class.java))
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            openMonitor()
                            SharedPerManager.setToken(loginEntity.data.token)
                            activity.finish()
                        }
                    }, { throwable ->
                        throwable.printStackTrace()
                        Log.i(TAG, "login fail====" + throwable.message)
                        MyToastView.getInstances().Toast(MirrorApplication.getInstance(), "登录失败\n")
                    })
        }
    }

    private fun openMonitor() {
        val intent = Intent()
        intent.action = "com.mirror.mobile.action.START_SERVICE_MONITOR"
        intent.`package` = "com.mirror.mobile"
        startService(intent)
    }


    fun checkUpdate() {
        //检查系统升级
        val runSystem = SystemUpdateRunnabel(AppInfo.FIREWARE_UPDATE_URL)
        executor.execute(runSystem)
        //检查app升级
        val runapp = AppUpdateRunnable(AppInfo.UPDATE_URL)
        executor.execute(runapp)
    }

    private fun getMainInfo() {
        val runnable_hair = VideoRunnable(AppInfo.CODE_HAIR, videoInfoRetrofitFactory)
        executor.execute(runnable_hair)
        val runnable_finger = VideoRunnable(AppInfo.CODE_FINGER, videoInfoRetrofitFactory)
        executor.execute(runnable_finger)
        val runnable_body = VideoRunnable(AppInfo.CODE_BODY, videoInfoRetrofitFactory)
        executor.execute(runnable_body)
        val runnable_ani = VideoRunnable(AppInfo.CODE_ANIM, videoInfoRetrofitFactory)
        executor.execute(runnable_ani)
        val getVideo_advs = GetAdvioAdvLists()
        executor.execute(getVideo_advs)
    }


    fun getExecutor(): Executor {
        return executor
    }

    /***
     * 文件下载
     * * @param isDelFile
     * 是否删除源文件
     * @param downUrl
     * 下载地址
     * @param saveUrl
     * 文件保存SD卡的地址
     */
    fun downFile(isDelFile: Boolean, downUrl: String, saveUrl: String) {
        val runnable = DownRunnable(downUrl, saveUrl, object : DownStateListener {
            override fun downStateInfo(entity: DownFileEntity) {
            }
        })
        runnable.setIsDelFile(isDelFile)  //删除源文件
        executor.execute(runnable)
    }

    /**
     * 加载视频列表
     *
     * @param subchannel_id
     * @param waitDialogUtil
     * @param videoViewAdapter
     */
    fun loadData(subchannel_id: Int, waitDialogUtil: WaitDialogUtil, videoViewAdapter: VideoViewAdapter) {
        val token = SharedPerManager.getToken()
        videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getVideoDatas(subchannel_id, token)
                .compose(RxJavaHelp.setThreadWithDialog(waitDialogUtil, "数据获取中..."))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ videoEntity ->
                    if (videoEntity != null && videoEntity.data != null) {
                        saveAppList(videoEntity.data, subchannel_id)
                        videoViewAdapter.setList(videoEntity.data)
                        videoViewAdapter.notifyDataSetChanged()
                    }
                }) { throwable ->
                    throwable.printStackTrace()
                    Log.e(TAG, "loadData FAIL====" + throwable.message)
                    MyToastView.getInstances().Toast(MirrorApplication.getInstance(), "加载失败，请检查网络情况")
                }
    }

    private fun saveAppList(lists: List<VideoEntity.DataBean>, subchannel_id: Int) {
        when (subchannel_id) {
            AppInfo.CODE_HAIR -> MirrorApplication.getInstance().setList_hair(lists)
            AppInfo.CODE_FINGER -> MirrorApplication.getInstance().setList_finger(lists)
            AppInfo.CODE_BODY -> MirrorApplication.getInstance().setList_body(lists)
            AppInfo.CODE_ANIM -> MirrorApplication.getInstance().setList_anim(lists)
        }
    }


    /**
     * 开始播放视频
     *
     * @param subchannel_id
     * @param advVideoId
     * @param playerAdvPosition
     * @param entity
     * @param mWaitDialogUtil
     */
    fun startVideo(subchannel_id: Int, advVideoId: Int, playerAdvPosition: String, entity: VideoEntity.DataBean, mWaitDialogUtil: WaitDialogUtil) {
        val token = SharedPerManager.getToken()
        videoInfoRetrofitFactory!!.providerHttpRequestData()!!.getVideoInfo(subchannel_id, playerAdvPosition, advVideoId, token)
                .compose(RxJavaHelp.setThreadWithDialog(mWaitDialogUtil, "视频信息加载中..."))
                .subscribe(Consumer<ADvideoEntity> { lists ->
                    if (lists != null && lists.data != null && lists.data.pos_11 != null) {
                        if (lists.data.pos_11.size > 0) {
                            val adVideoPath = lists.data.pos_11[0].videopath
                            val advId = Integer.parseInt(lists.data.pos_11[0].id)
                            val timelength = Integer.parseInt(lists.data.pos_11[0].timelength)
                            val adTitle = lists.data.pos_11[0].title
                            startPlayVideo(entity, adVideoPath, 0, timelength, adTitle)
                            return@Consumer
                        }
                    }
                    throw Exception("data is null")
                }, Consumer<Throwable> {
                    retrofitFactory!!.providerHttpRequestData().getDefaultInfo()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(Consumer<DefaultAdvEntity> { defaultAdvEntity ->
                                val adUrl = defaultAdvEntity.adurl
                                val advId = Integer.parseInt(defaultAdvEntity.id)
                                val timeLength = Integer.parseInt(defaultAdvEntity.timelength)
                                startPlayVideo(entity, adUrl, advId, timeLength, defaultAdvEntity.adtitle)
                            }, Consumer<Throwable> {
                                MyToastView.getInstances().Toast(MirrorApplication.getInstance(), "加载错误，请检查网络")
                                startPlayVideo(entity, AppInfo.PLAY_ADV, 0, 15, "尽善镜美官方招商视频-媒体篇")
                            })
                })
    }

    fun startPlayVideo(entity: VideoEntity.DataBean, adVideoPath: String, advId: Int, advTime: Int, advTitle: String) {
        val intent = Intent(MirrorApplication.getInstance(), VideoPlayActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(VideoPlayActivity.PLAY_URL, entity.videopath)
        intent.putExtra(VideoPlayActivity.ADV_URL, adVideoPath)
        intent.putExtra(VideoPlayActivity.VIDEO_TITLE, entity.title)
        intent.putExtra(VideoPlayActivity.VIDEO_ID, entity.id)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_ID, advId)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_Time, advTime)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_TITLE, advTitle)
        MyLog.e("HairFragment", "====点击进入====" + entity.videopath + "\n===" + adVideoPath)
        startActivity(intent)
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    private fun initReceiver() {
        val filter = IntentFilter()
        filter.addAction(KEYCODE_HOME)
        filter.addAction(KEYCODE_OPEN)
        filter.addAction(BOOT_OPEN)
        filter.addAction(LOGIN_IM_SUCCESS_TYPE)
        filter.addAction(VOLUEM_CHANGE)
        registerReceiver(receiver, filter)
    }

    override fun querySuccess(list_video: List<VideoEntity>) {

    }

    override fun queryTvSuccess(tvAbroadInfos: List<ItemTv>) {

    }

    override fun queryFaled(desc: String) {

    }

    override fun queryFailed(desc: String) {

    }

    override fun queryError(desc: String) {

    }

    override fun parsJsonError(error: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPerManager.setLogin(false)
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
    }
}