package com.mirroproject.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import com.mirroproject.R
import com.mirroproject.adapter.LiveAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.app.DaggerAppComponent
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.LiveEntity
import com.mirroproject.util.APKUtil
import com.mirroproject.util.DownManager
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/1.
 */

class LiveFragment : Fragment(), AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    private var liveView: GridView? = null
    private var liveAdapter: LiveAdapter? = null
    lateinit var liveEntityList: ArrayList<LiveEntity>
    @Inject
    lateinit var downManager: DownManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        liveEntityList = ArrayList<LiveEntity>()
        liveEntityList.add(LiveEntity(AppInfo.DOUYU_NAME_APP, AppInfo.DOUYU_ICON, AppInfo.DOUYU_DOWNURL, AppInfo.DOUYU_PACKAGE, ""))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(activity, R.layout.fragment_live, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        DaggerAppComponent.builder().appModule(AppModule(getActivity())).build().inject(this)
        liveView = view.findViewById<View>(R.id.live) as GridView
        liveAdapter = LiveAdapter(activity)
        liveAdapter!!.setLiveEntityList(liveEntityList)
        liveView!!.adapter = liveAdapter
        liveView!!.onItemClickListener = this
        liveView!!.onItemSelectedListener = this
        liveView!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                liveAdapter!!.setSelector(true)
                liveAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.i("fragment", "=====item点击事件====")
        openLive(position)
    }

    fun openLive(position: Int) {
        val liveEntity = liveEntityList[position]
        val isInstall = APKUtil.ApkState(activity, liveEntity.packageName)
        Log.i("package----", liveEntity.packageName)
        if (isInstall) {
            val manager = activity.packageManager
            val openAPK = manager.getLaunchIntentForPackage(liveEntity.packageName)
            startActivity(openAPK)
        } else {
            var savePath = ""
            var des = ""
            if (liveEntity.liveName.contains(AppInfo.DOUYU_NAME_APP)) {
                savePath = AppInfo.BASE_APK_PATH + "/douyu.apk"
                des = "是否下载 <" + AppInfo.DOUYU_NAME_APP + "> 软件"
            } else if (liveEntity.liveName.contains(AppInfo.YY_NAME_APP)) {
                savePath = AppInfo.BASE_APK_PATH + "/yylive.apk"
                des = "是否下载 <" + AppInfo.YY_NAME_APP + "> 软件"
            }
            Log.i("url-----", liveEntity.downLoadUrl)
            downManager!!.downSatrt(liveEntity.downLoadUrl, savePath, des)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        for (i in 0 until liveView!!.count) {
            Log.i("test=======", "position:" + i)
            if (i == position) {
                liveView!!.getChildAt(position).setBackgroundResource(R.drawable.rect_circle_blue)
            } else {
                liveView!!.getChildAt(i).setBackgroundResource(R.drawable.rect_circle_tranclate)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        //        parent.setBackgroundResource(R.drawable.rect_circle_tranclate);
    }
}
