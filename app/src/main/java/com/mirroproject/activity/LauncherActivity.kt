package com.mirroproject.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.mirroproject.R
import com.mirroproject.adapter.LauncherAdapter
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.LaunchEntity
import com.mirroproject.http.HttpRequest
import com.mirroproject.util.APKUtil
import com.mirroproject.util.CheckUpdateUtil
import com.mirroproject.util.DownManager
import java.util.ArrayList

/**
 * Created by reeman on 2017/10/31.
 */

class LauncherActivity : BaseActivity(), AdapterView.OnItemClickListener {


    private var grid_launcher: GridView? = null
    private var httpGet: HttpRequest? = null
    private var checkUpdateUtil: CheckUpdateUtil? = null


    internal var lists: MutableList<LaunchEntity> = ArrayList<LaunchEntity>()
    internal var adapter: LauncherAdapter? = null
    private var downManager: DownManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        getDataInfo()
        initView()
    }

    private fun getDataInfo() {
        lists.clear()
        lists.add(LaunchEntity(R.mipmap.launcher_file))
        lists.add(LaunchEntity(R.mipmap.launcher_setting))
        lists.add(LaunchEntity(R.mipmap.launcher_sea))
        lists.add(LaunchEntity(R.mipmap.launcher_update))
    }

    private fun initView() {
        httpGet = HttpRequest()
        downManager = DownManager(this)
        checkUpdateUtil = CheckUpdateUtil(httpGet as HttpRequest)
        grid_launcher = findViewById<View>(R.id.grid_launcher) as GridView
        adapter = LauncherAdapter(this@LauncherActivity, lists)
        grid_launcher!!.adapter = adapter
        grid_launcher!!.onItemClickListener = this
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        when (i) {
            0 -> {   //文件管理器
                val intent1 = Intent(Intent.ACTION_MAIN)
                intent1.addCategory(Intent.CATEGORY_LAUNCHER)
                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val cn1 = ComponentName("com.android.rockchip", "com.android.rockchip.RockExplorer")
                intent1.component = cn1
                startActivity(intent1)
            }
            1 -> {  //设置界面
                val openSetting = Intent(Settings.ACTION_SETTINGS)
                openSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(openSetting)
            }
            2 -> {  //监控界面
                val isState = APKUtil.ApkState(this@LauncherActivity, AppInfo.VIDEO_MONITOR_PACKAGE_NAME)
                if (!isState) {
                    val downUrl = AppInfo.VIDEO_MONITOR_URL
                    downManager!!.downSatrt(downUrl, AppInfo.VIDEO_MONITOR_DOWNLOAD_SAVE_APK, "是否下载 <视频监控> 软件")
                } else {
                    //打开监控app
                    val intent2 = Intent(Intent.ACTION_MAIN)
                    intent2.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val cn2 = ComponentName("com.mirror.mobile", "com.mirror.mobile.activity.QrCodeActivity")
                    intent2.component = cn2
                    startActivity(intent2)
                }
            }
            3 -> startActivity(Intent(this@LauncherActivity, UpdateActiivty::class.java))
        }
    }

    companion object {
        private val UPDATE = 3
    }
}
