package com.mirroproject.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirroproject.R
import com.mirroproject.adapter.VideoViewAdapter
import com.mirroproject.app.AppModule
import com.mirroproject.app.DaggerAppComponent
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.VideoEntity
import com.mirroproject.http.HttpRequest
import com.mirroproject.service.MirrorService
import com.mirroproject.util.MyLog
import com.mirroproject.view.WaitDialogUtil
import com.mirroproject.view.recycleview.SWRecyclerView
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by reeman on 2017/11/1.
 */

class HairFragment : Fragment(), VideoViewAdapter.OnItemClickListener {

    lateinit var rv_home_list: SWRecyclerView
    internal var list_content: List<VideoEntity.DataBean> = ArrayList()
    private var homeListAdapter: VideoViewAdapter? = null
    @Inject
    lateinit var httpRequest: HttpRequest
    @Inject
    lateinit var mWaitDialogUtil: WaitDialogUtil

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(activity, R.layout.fragment_video, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        DaggerAppComponent.builder().appModule(AppModule(activity)).build().inject(this)
        list_content = MirrorApplication.getInstance().getList_hair()
        rv_home_list = view.findViewById<View>(R.id.rv_home_list) as SWRecyclerView
        homeListAdapter = VideoViewAdapter(activity, list_content)
        homeListAdapter!!.setOnItemClickListener(this)
        rv_home_list.setLayoutManager(LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false))
        rv_home_list.setAdapter(homeListAdapter)
        jujleListContent(list_content)
        Log.i("HairFragment=====", "count===" + list_content.size)
    }

    override fun onItemClick(view: View, entity: VideoEntity.DataBean) {
        mWaitDialogUtil!!.show("视频信息加载中...")
        val subchannel_id = Integer.parseInt(entity.subchannel_id)
        val advVideoId = Integer.parseInt(entity.id)
        val playerAdvPosition = "8,9,10,11"
        MirrorService.getInstance().startVideo(subchannel_id, advVideoId, playerAdvPosition, entity, mWaitDialogUtil)
    }

    private fun jujleListContent(list_content: List<VideoEntity.DataBean>) {
        if (list_content.size < 1) {
            MyLog.i("MirrorService", "==检车数据是为空,联网查询")
            MirrorService.getInstance().loadData(AppInfo.CODE_HAIR, mWaitDialogUtil, homeListAdapter!!)
        }
    }
}
