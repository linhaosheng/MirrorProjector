package com.mirroproject.activity.main

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import com.mirroproject.R
import com.mirroproject.activity.ExitActivity
import com.mirroproject.activity.UpdateActiivty
import com.mirroproject.activity.video.VideoPlayActivity
import com.mirroproject.adapter.MainGridAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.*
import com.mirroproject.fragment.*
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.HttpRequestListener
import com.mirroproject.util.*
import com.mirroproject.view.ADView
import com.mirroproject.view.DialogConfirm
import com.mirroproject.view.MyToastView
import com.mirroproject.view.SystemDialog
import com.mirroproject.view.ad.ADUtil
import com.mirroproject.view.ad.CycleViewPager
import com.mirroproject.view.down.UpdateView
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/3.
 */
class MainActivityHelp(val mainActivity: MainActivity, var handler: Handler, val mainActivityView: MainActivityView) {
    private var lists: List<MainEntity>? = null
    lateinit var adapter_grid: MainGridAdapter
    private var infos: ArrayList<ADInfo.Pos1Bean> = ArrayList()
    private var pos_3: ArrayList<ADInfo.Pos3Bean> = ArrayList()
    private val updateInfo: UpdateInfo? = null
    private var token: String? = null
    private val videoId = 0
    private var adInfo: ADInfo? = null
    private var advIndex: Int = 0
    private var advText: String? = null
    internal var laatGridPoint = 0
    lateinit var adView: ADView
    internal var fragmentManager: FragmentManager

    @Inject
    lateinit var mainActivityBussin: MainActivityBussin
    @Inject
    lateinit var downManager: DownManager
    @Inject
    lateinit var systemDialog: SystemDialog
    @Inject
    lateinit var dialogConfirm: DialogConfirm
    @Inject
    lateinit var httpGet: HttpRequest

    internal var hairFragment: HairFragment? = null
    internal var fingerFragment: FingerFragment? = null
    internal var mBodyFragment: BodyFragment? = null
    internal var mCarTonFragment: CarTonFragment? = null
    internal var mHairDbFragment: HairDbFragment? = null
    internal var mLiveFragment: LiveFragment? = null
    internal var mTVFragment: TVFragment? = null
    internal var mScreenFragment: ScreenFragment? = null
    internal var mOutsideFragment: OutsideFragment? = null

    init {
        fragmentManager = mainActivity.fragmentManager
        DaggerMainComponent.builder().mainModule(MainModule(mainActivity, handler, mainActivityView)).appModule(AppModule(mainActivity)).build().inject(this)
        initData()
        initView()
        mainActivityBussin!!.iniIp()
    }

    fun setMainGridAdapter(adapter_grid: MainGridAdapter) {
        this.adapter_grid = adapter_grid
    }

    fun setLaatGridPoint(laatGridPoint: Int) {
        this.laatGridPoint = laatGridPoint
    }

    fun setList(lists: List<MainEntity>) {
        this.lists = lists
    }

    private fun initView() {
        systemDialog = SystemDialog(mainActivity)
        systemDialog.setSystemDialogListener(object : SystemDialog.SystemDialogListener {
            override fun update(tag: Int) {
                systemDialog.dismiss()
                mainActivity.startActivity(Intent(mainActivity, UpdateActiivty::class.java))
            }
        })
    }

    private fun initData() {
        SharedPerManager.setLogin(true)
        ADUtil.getAdList(handler)
        downManager = DownManager(mainActivity)
        token = SharedPerManager.getToken()
        dialogConfirm!!.setTitle("提醒!")
        dialogConfirm!!.hideSpeed()
        dialogConfirm!!.setBtnText("确定", "取消")
    }

    /***
     * 升级系统
     * @param systemUpdateInfo
     */
    fun updateSystem(systemUpdateInfo: SystemUpdateInfo) {
        val currentSysCode = Build.VERSION.INCREMENTAL.trim { it <= ' ' }.trim { it <= ' ' }
        val webCode = systemUpdateInfo.systemcode ?: //错误的返回信息也会从这里返回来，这里加一个标记
                return
        if (currentSysCode != webCode) {
            systemDialog.show("当前检测到新系统软件，是否前去升级 ?", UpdateView.TAG_SYSTEM_UPDATE)
        }
    }

    /***
     * 软件升级网络请求返回
     * @param updateInfo
     */
    fun updateApp(updateInfo: UpdateInfo) {
        val currentCode = APKUtil.getVersionCode(mainActivity)
        val webCode = updateInfo.appversion
        if (webCode == -1) {   //-1表示错误的网络请求
            return
        }
        if (webCode > currentCode) {
            systemDialog.show("当前检测到新的软件，是否前去升级 ?", UpdateView.TAG_APP_UPDATE)
        }
    }


    fun updateEvent(updateEventType: EventType.UpdateEventType) {
        when (updateEventType.updateType) {
            CheckUpdateUtil.SYSTEM_UPDATE -> {
                systemDialog.setSureButton("升级")
                systemDialog.show("检查到系统升级", 0)
            }
            CheckUpdateUtil.SOFE_UPDATE -> systemDialog.show("检查到软件升级", 0)
            CheckUpdateUtil.UPDATE_FAIL -> MyToastView.getInstances().Toast(mainActivity, "获取版本失败")
        }
    }

    fun getAdvListData(msg: Message) {
        adInfo = msg.obj as ADInfo
        infos = adInfo!!.data.pos_1
        pos_3 = adInfo!!.data.pos_3
        if (infos!!.size == 0) {
            val pos1Bean = ADInfo.Pos1Bean("", "", "", "", "", "", "", "", "", "", "10")
            infos!!.add(pos1Bean)
        }
        if (infos != null && infos!!.size > 0) {
            adView = ADView(mainActivity, mainActivityView.cycleViewPager(), infos)
            adView.setHttpRequest(httpGet)
        }
        if (pos_3.size == 0) {
            val pos3Bean = ADInfo.Pos3Bean("", "", "", "", "镜善镜美商务合作", "", "", "", "", "", "15")
            pos_3.add(pos3Bean)
        }
        MirrorApplication.getInstance().setInfos(infos)
        MirrorApplication.getInstance().setPos_3(pos_3)
    }

    fun getAdvError(msg: Message) {
        val err = msg.obj as String
        if (infos!!.size == 0) {
            val pos1Bean = ADInfo.Pos1Bean("", "", "", "", "", "", "", "", "", "", "10")
            infos!!.add(pos1Bean)
        }
        if (infos != null && infos!!.size > 0) {
            adView = ADView(mainActivity, mainActivityView.cycleViewPager(), infos)
            adView.setHttpRequest(httpGet)
        }
        MyToastView.getInstances().Toast(mainActivity, "获取广告错误")
    }


    fun startDownLoad() {
        downManager.downSatrt(updateInfo!!.apkurl, AppInfo.DOWNLOAD_SAVE_APK, updateInfo!!.updatedesc)
    }

    fun cycle(type: EventType.CycleEventType) {
        val viewIsFront = ActivityCollector.isForeground(mainActivity, mainActivity.localClassName)
        if (type.cycleType === 1 && viewIsFront && !VideoPlayActivity.isPlayTv) {
            Log.i("cycle---text", CycleViewPager.position.toString())
            var text = ""
            advIndex = mainActivityView.cycleViewPager().getCurrentPostion()
            if (advIndex > pos_3.size - 1) {
                advIndex = 0
            }
            if (pos_3.size == 0) {
                text = "镜善镜美商务合作"
            } else {
                text = pos_3[advIndex].text
            }
            if (text != advText && mainActivityView.marqueeView().isShown()) {
                advText = text
                mainActivityView.marqueeView().setText(text)
            }

            Log.i(TAG, "wheelTime=======" + CycleViewPager.wheelTime)
            if (infos!![mainActivityView.cycleViewPager().getCurrentPostion()].id == null)
                return
            val id = Integer.parseInt(infos!![mainActivityView.cycleViewPager().getCurrentPostion()].id)
            httpGet!!.advPlayCount(token!!, videoId, id, false, object : HttpRequestListener {
                override fun requestSuccess(jsonRequest: String) {
                    //   MyToastView.getInstance().Toast(MainActivity.this, jsonRequest);
                }

                override fun requestFailed(error: String) {

                }
            })
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //点击，如果当期显示屏保，则关闭
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) { //右
            if (mainActivityView.gridView().isFocused()) {
                for (i in lists!!.indices) {
                    lists!![i].isCheck = false
                }
                adapter_grid.notifyDataSetChanged()
            }
            if (mainActivityView._rela_db_hair().isFocused()) {
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) { //左
            if (mainActivityView._rela_db_hair().isFocused()) {
                lists!![laatGridPoint].isCheck = true
                adapter_grid.notifyDataSetChanged()
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            MyLog.i(TAG, "您点击的向下的遥控按钮")
            mainActivityView.gridView().requestFocus()
            for (i in lists!!.indices) {
                lists!![i].isCheck = false
            }
            lists!![laatGridPoint].isCheck = true
            adapter_grid.notifyDataSetChanged()
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            //点击向上的按钮，底部菜单全部失去焦点
            for (i in lists!!.indices) {
                lists!![i].isCheck = false
            }
            adapter_grid.notifyDataSetChanged()
            MyLog.i(TAG, "您点击的向上的遥控按钮")
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogConfirm!!.show("是否要退出主界面", KEY_BACK_CODE)
            return true
        }
        return false
    }

    fun exitEvent(exitType: EventType.ExitType) {
        if (exitType.exitType === KEY_BACK_CODE) {
            dialogConfirm!!.dimiss()
            mainActivity.startActivity(Intent(mainActivity, ExitActivity::class.java))
        }
    }

    fun setTabSelection(index: Int) {
        val transaction = fragmentManager.beginTransaction()
        hideFragments(transaction)
        Log.i("index-----", index.toString() + "")
        when (index) {
            HairFragment_code  //美发
            -> if (hairFragment == null) {
                hairFragment = HairFragment()
                transaction.add(R.id.frame_content, hairFragment as Fragment)
            } else {
                transaction.show(hairFragment as Fragment)
            }
            FingerFragment_code  //美jia
            -> if (fingerFragment == null) {
                fingerFragment = FingerFragment()
                transaction.add(R.id.frame_content, fingerFragment as Fragment)
            } else {
                transaction.show(fingerFragment as Fragment)
            }
            BodyFragment_code  //美体
            -> if (mBodyFragment == null) {
                mBodyFragment = BodyFragment()
                transaction.add(R.id.frame_content, mBodyFragment as Fragment)
            } else {
                transaction.show(mBodyFragment as Fragment)
            }
            TVFragment_code  //电视
            -> if (mTVFragment == null) {
                mTVFragment = TVFragment()
                transaction.add(R.id.frame_content, mTVFragment as Fragment)
            } else {
                transaction.show(mTVFragment as Fragment)
            }
            CarTonFragment_code  //卡通
            -> if (mCarTonFragment == null) {
                mCarTonFragment = CarTonFragment()
                transaction.add(R.id.frame_content, mCarTonFragment as Fragment)
            } else {
                transaction.show(mCarTonFragment as Fragment)
            }
            OutsizeFragment_code  //外接
            -> if (mOutsideFragment == null) {
                mOutsideFragment = OutsideFragment()
                transaction.add(R.id.frame_content, mOutsideFragment as Fragment)
            } else {
                transaction.show(mOutsideFragment as Fragment)
            }
            LiveFragment_code  //直播
            -> if (mLiveFragment == null) {
                mLiveFragment = LiveFragment()
                transaction.add(R.id.frame_content, mLiveFragment as Fragment)
            } else {
                transaction.show(mLiveFragment as Fragment)
            }
            ScreenFragment_code  //投屏
            -> if (mScreenFragment == null) {
                mScreenFragment = ScreenFragment()
                transaction.add(R.id.frame_content, mScreenFragment as Fragment)
            } else {
                transaction.show(mScreenFragment as Fragment)
            }
            HairDbFragment_code  //发行库
            -> if (mHairDbFragment == null) {
                mHairDbFragment = HairDbFragment()
                transaction.add(R.id.frame_content, mHairDbFragment as Fragment)
            } else {
                transaction.show(mHairDbFragment as Fragment)
            }
        }
        transaction.commit()
    }

    fun hideFragments(transaction: FragmentTransaction) {
        if (hairFragment != null) {
            transaction.hide(hairFragment as Fragment)
        }
        if (fingerFragment != null) {
            transaction.hide(fingerFragment as Fragment)
        }
        if (mBodyFragment != null) {
            transaction.hide(mBodyFragment as Fragment)
        }
        if (mCarTonFragment != null) {
            transaction.hide(mCarTonFragment as Fragment)
        }
        if (mHairDbFragment != null) {
            transaction.hide(mHairDbFragment as Fragment)
        }
        if (mLiveFragment != null) {
            transaction.hide(mLiveFragment as Fragment)
        }
        if (mTVFragment != null) {
            transaction.hide(mTVFragment as Fragment)
        }
        if (mScreenFragment != null) {
            transaction.hide(mScreenFragment as Fragment)
        }
        if (mOutsideFragment != null) {
            transaction.hide(mOutsideFragment as Fragment)
        }
    }

    companion object {
        val TAG = "MainActivity"
        private val KEY_BACK_CODE = 1
        val HairFragment_code = 0
        val FingerFragment_code = 1
        val BodyFragment_code = 2
        val TVFragment_code = 3
        val CarTonFragment_code = 4
        val OutsizeFragment_code = 5
        val LiveFragment_code = 6
        val ScreenFragment_code = 7
        val HairDbFragment_code = 8
    }
}

