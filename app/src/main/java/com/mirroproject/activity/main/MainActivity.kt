package com.mirroproject.activity.main

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import butterknife.ButterKnife
import com.mirroproject.R
import com.mirroproject.activity.BaseActivity
import com.mirroproject.activity.video.VideoPlayActivity
import com.mirroproject.adapter.MainGridAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.EventType
import com.mirroproject.entity.MainEntity
import com.mirroproject.entity.SystemUpdateInfo
import com.mirroproject.entity.UpdateInfo
import com.mirroproject.service.MirrorService
import com.mirroproject.service.PopupService
import com.mirroproject.util.MainUtil
import com.mirroproject.view.MarqueeView
import com.mirroproject.view.ad.CycleViewPager
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener, View.OnHoverListener, MainActivityView {

    private var lists: List<MainEntity> = ArrayList<MainEntity>()

    lateinit var adapter_grid: MainGridAdapter
    var _rela_db_hair: RelativeLayout? = null
    lateinit var frame_content: FrameLayout
    lateinit var cycleViewPager: CycleViewPager
    var gridView: GridView? = null
    private var fragmentPosition: Int = 0
    lateinit var marqueeView: MarqueeView
    @Inject
    lateinit var mainActivityHelp: MainActivityHelp
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val what = msg.what
            when (what) {
                1 -> mainActivityHelp!!.getAdvListData(msg)
                2 -> mainActivityHelp!!.getAdvError(msg)
                UPDATE -> mainActivityHelp!!.startDownLoad()
            }
        }
    }
    internal var laatGridPoint = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        EventBus.getDefault().register(this)
        initView()
        initFragment(savedInstanceState)
        MirrorService.getInstance().checkUpdate()
        startService(Intent(this@MainActivity, PopupService::class.java))
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("position", fragmentPosition)
    }

    /***
     * 升级系统
     * @param systemUpdateInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateSystem(systemUpdateInfo: SystemUpdateInfo) {
        Log.i(TAG, "===system=====")
        mainActivityHelp!!.updateSystem(systemUpdateInfo)
    }

    /***
     * 软件升级网络请求返回
     * @param updateInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateApp(updateInfo: UpdateInfo) {
        Log.i(TAG, "===app=====")
        mainActivityHelp!!.updateApp(updateInfo)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateEvent(updateEventType: EventType.UpdateEventType) {
        mainActivityHelp!!.updateEvent(updateEventType)
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            mainActivityHelp!!.setTabSelection(0)
        } else {
            val position = savedInstanceState.getInt("position")
            mainActivityHelp!!.setTabSelection(position)
        }
    }

    private fun initView() {
        marqueeView = findViewById<View>(R.id.tv_mv_notice) as MarqueeView
        //   mainActivityHelp = new MainActivityHelp(MainActivity.this, handler, this);
        DaggerMainComponent.builder().mainModule(MainModule(this@MainActivity, handler, this)).appModule(AppModule(this)).build().inject(this)
        lists = MainUtil.getMainList1()
        adapter_grid = MainGridAdapter(this@MainActivity, lists)
        gridView!!.adapter = adapter_grid
        gridView!!.onItemSelectedListener = this
        gridView!!.onItemClickListener = this
        //==========================发型库===============================
        _rela_db_hair!!.setOnClickListener(this)
        _rela_db_hair!!.setOnHoverListener(this)
        tv_title_show!!.setText(lists[0].titleName)
        frame_content!!.isFocusable = false
        gridView!!.requestFocus()
        //====================广告位展示区======================
        cycleViewPager = fragmentManager
                .findFragmentById(R.id.fragment_cycle_viewpager_content) as CycleViewPager
        //=====跑马灯============================================================
        marqueeView!!.startScroll("MainActivity")
        val bitmap = BitmapFactory.decodeFile(AppInfo.ER_CODE_PATH)
        device_er_code!!.setImageBitmap(bitmap)
        device_er_code!!.visibility = View.GONE
        mainActivityHelp!!.setMainGridAdapter(adapter_grid)
        mainActivityHelp!!.setList(lists)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cycle(type: EventType.CycleEventType) {
        mainActivityHelp!!.cycle(type)
    }

    override fun onResume() {
        super.onResume()
        if (!marqueeView!!.isRun) {
            marqueeView!!.resumeScroll()
        }
        VideoPlayActivity.isPlayTv = false
        if (cycleViewPager != null) {
            cycleViewPager!!.setIsSendEvent(true)
        }
    }

    override fun onPause() {
        super.onPause()
        marqueeView()!!.stopScroll()
        if (cycleViewPager != null) {
            cycleViewPager!!.setIsSendEvent(false)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.rela_db_hair -> mainActivityHelp!!.setTabSelection(HairDbFragment_code)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.i("main", "====您点击的菜单按钮===" + keyCode)
        return mainActivityHelp!!.onKeyDown(keyCode, event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun exitEvent(exitType: EventType.ExitType) {
        mainActivityHelp!!.exitEvent(exitType)
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
        tv_title_show!!.setText(lists[position].titleName)
        fragmentPosition = position
        mainActivityHelp!!.setTabSelection(position)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
        laatGridPoint = position
        mainActivityHelp!!.setLaatGridPoint(laatGridPoint)
        for (i in lists.indices) {
            lists[i].isCheck = false
        }
        lists[position].isCheck = true
        adapter_grid.notifyDataSetChanged()
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
    }

    override fun onHover(view: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action
        when (action) {
            MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
            -> view.setBackgroundResource(R.drawable.rect_circle_yellow)
            MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
            -> view.setBackgroundResource(R.drawable.rect_circle_tranclate)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this@MainActivity, PopupService::class.java))
        EventBus.getDefault().unregister(this)
    }

    companion object {
        val TAG = "MainActivity"
        private val UPDATE = 3

        val HairDbFragment_code = 8
    }

    override fun cycleViewPager(): CycleViewPager {
        return cycleViewPager
    }

    override fun marqueeView(): MarqueeView {
        return marqueeView
    }

    override fun gridView(): GridView {
        return grid_menu_main
    }

    override fun _rela_db_hair(): RelativeLayout {
        return rela_db_hair
    }
}
