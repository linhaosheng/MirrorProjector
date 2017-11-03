package com.mirroproject.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.mirroproject.R
import com.mirroproject.config.AppInfo
import com.mirroproject.util.*
import kotlinx.android.synthetic.main.fragment_touping.*
import java.io.File

/**
 * Created by reeman on 2017/11/1.
 */

class ScreenFragment : Fragment(), View.OnClickListener, View.OnHoverListener {


    private val button: Button? = null
    private var btn_entry: Button? = null
    lateinit var downManager: DownManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(activity, R.layout.fragment_touping, null)
        initTouPingView()
        return view
    }

    private fun initTouPingView() {
        val wifiName = NetWorkUtils.getConnectName(activity)
        Log.i("wifiName=====", wifiName)
        conn_wifi!!.setText(wifiName)
        var deviIdInfo = ""
        if (!SharedPerManager.getDeviceId().contains("尽善镜美")) {
            deviIdInfo = "尽善镜美-" + CodeUtil.getDeviceID()
          //  EShareUtil.setDeviceName(activity, deviIdInfo)
            val i = Intent("com.eshare.action.DEVICE_NAME_CHANGED")
            activity.sendBroadcast(i)
            SharedPerManager.setDeviceId(deviIdInfo)
        } else {
            deviIdInfo = SharedPerManager.getDeviceId()
        }
        devi_ID!!.text = deviIdInfo
        if (File(AppInfo.TOU_PING_ER_CODE_PATH).exists()) {
            val bitmap = BitmapFactory.decodeFile(AppInfo.TOU_PING_ER_CODE_PATH)
            touping_er!!.setImageBitmap(bitmap)
        }
    }

    private fun initView(view: View) {
        downManager = DownManager(activity)
        btn_entry = view.findViewById<View>(R.id.btn_entry) as Button
        btn_entry!!.setOnClickListener(this)
        btn_entry!!.setOnHoverListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_entry) {
            val isIsntall = APKUtil.ApkState(activity, AppInfo.LETOU_PACKAGE)
            if (!isIsntall) {
                val downUrl = AppInfo.LETOU_DOWN_URL
                val savePath = AppInfo.BASE_APK_PATH + "/letou.apk"
                downManager.downSatrt(downUrl, savePath, "是否下载<乐播投屏>软件")
            } else {
                val manager = activity.packageManager
                val openAPK = manager.getLaunchIntentForPackage(AppInfo.LETOU_PACKAGE)
                startActivity(openAPK)
            }
        }
    }

    override fun onHover(view: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action
        when (action) {
            MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
            -> view.setBackgroundResource(R.drawable.rect_circle_blue)
            MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
            -> view.setBackgroundResource(R.drawable.rect_circle_grey)
        }
        return false
    }

}
