package com.mirroproject.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import com.mirroproject.R
import com.mirroproject.activity.video.VideoPlayActivity
import com.mirroproject.adapter.LiveAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.app.DaggerAppComponent
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.LiveEntity
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.HttpRequestListener
import com.mirroproject.util.APKUtil
import com.mirroproject.util.DownManager
import com.mirroproject.util.GsonParse
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/1.
 */

class TVFragment : Fragment(), AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private var liveView: GridView? = null
    private var liveAdapter: LiveAdapter? = null
    lateinit var liveEntityList: ArrayList<LiveEntity>
    private var advUrl: String? = null
    internal var advId: Int = 0
    internal var timeLength: Int = 0
    lateinit var advTitle: String
    internal var getAdvError: Boolean = false
    @Inject
    lateinit var httpRequest: HttpRequest
    @Inject
    lateinit var downManager: DownManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        getAdvUrl()
    }

    private fun initData() {
        DaggerAppComponent.builder().appModule(AppModule(activity)).build().inject(this)
        liveEntityList = ArrayList()
        liveEntityList.add(LiveEntity(AppInfo.TV_HOME_NAME, AppInfo.TV_HOME_ICON, AppInfo.TV_HOME_DOWNURL, AppInfo.TV_HOME_PACKAGE, ""))
        liveEntityList.add(LiveEntity(AppInfo.BEE_NAME_APP, AppInfo.BEE_ICON, AppInfo.BEE_DOWNURL, AppInfo.BEE_PACKAGE, ""))
    }

    private fun getAdvUrl() {
        httpRequest!!.httpGet(AppInfo.DEFAULT_URL, object : HttpRequestListener {
            override fun requestSuccess(jsonRequest: String) {
                try {
                    val defaultAdvEntity = GsonParse.parseDefaultAdv(jsonRequest)
                    advUrl = defaultAdvEntity!!.adurl
                    advId = Integer.parseInt(defaultAdvEntity.id)
                    timeLength = Integer.parseInt(defaultAdvEntity.timelength)
                    advTitle = defaultAdvEntity.adtitle
                } catch (e: Exception) {
                    getAdvError = true
                    e.printStackTrace()
                }

                getAdvError = false
            }

            override fun requestFailed(error: String) {
                getAdvError = true
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(activity, R.layout.fragment_live, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
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
        openLive(position)
    }

    fun openLive(position: Int) {
        val liveEntity = liveEntityList[position]
        val isInstall = APKUtil.ApkState(activity, liveEntity.packageName)
        if (isInstall) {
            gotoVideoPlayActivity(liveEntity)
        } else {
            var savePath = ""
            var des = ""
            if (liveEntity.liveName.contains(AppInfo.TV_HOME_NAME)) {
                savePath = AppInfo.BASE_APK_PATH + "/tvhome.apk"
                des = "是否下载 <" + AppInfo.TV_HOME_NAME + "> 软件"
            } else if (liveEntity.liveName.contains(AppInfo.BEE_NAME_APP)) {
                savePath = AppInfo.BASE_APK_PATH + "/bee.apk"
                des = "是否下载 <" + AppInfo.BEE_NAME_APP + "> 软件"
            }
        }
    }

    private fun gotoVideoPlayActivity(liveEntity: LiveEntity) {
        Log.i("fragment", "=====item点击事件====")
        val intent = Intent(activity, VideoPlayActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("liveEntity", liveEntity)
        if (getAdvError) {
            intent.putExtra(VideoPlayActivity.ADV_URL, advUrl)
            //  Log.i("advUrl======", advUrl);
        }
        intent.putExtras(bundle)
        intent.putExtra(VideoPlayActivity.VIDEO_ID, 0)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_ID, advId)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_TITLE, advTitle)
        intent.putExtra(VideoPlayActivity.VIDEO_ADV_Time, timeLength)
        intent.putExtra(VideoPlayActivity.ADV_URL, advUrl)
        if (advUrl != null) {
            Log.i("advUrl======", advUrl)
        }
        startActivity(intent)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        for (i in 0 until liveView!!.count) {
            Log.i("test=======", "position:" + i)
            if (i == position) {
                liveView!!.getChildAt(position).setBackgroundResource(R.drawable.rect_circle_bordle5)
            } else {
                liveView!!.getChildAt(i).setBackgroundResource(R.drawable.rect_circle_tranclate)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
