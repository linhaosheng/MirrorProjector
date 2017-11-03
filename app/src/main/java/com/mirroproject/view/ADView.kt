package com.mirroproject.view

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.mirroproject.entity.ADInfo
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.HttpRequestListener
import com.mirroproject.util.SharedPerManager
import com.mirroproject.view.ad.CycleViewPager
import com.mirroproject.view.ad.ViewFactory
import java.util.ArrayList

/**
 * Created by reeman on 2017/11/3.
 */

class ADView(internal var context: Context, cycleViewPager: CycleViewPager, infos: ArrayList<ADInfo.Pos1Bean>) : CycleViewPager.ImageCycleViewListener {

    private val views = ArrayList<ImageView>()
    private var httpRequest: HttpRequest? = null

    fun setHttpRequest(httpRequest: HttpRequest) {
        this.httpRequest = httpRequest
    }

    init {
        // 将最后一个ImageView添加进来
        //  views.add(ViewFactory.getImageView(context, infos.get(infos.size() - 1).getPicpath()));
        for (i in infos.indices) {
            views.add(ViewFactory.getImageView(context, infos[i].picpath))
        }
        // 将第一个ImageView添加进来
        //   views.add(ViewFactory.getImageView(context, infos.get(0).getPicpath()));
        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true)
        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, this)
        //设置轮播
        cycleViewPager.setWheel(true)
        // 设置轮播时间，默认5000ms
        val time = Integer.parseInt(infos[0].timelength)
        cycleViewPager.setTime(time * 1000)
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter()
    }

    override fun onImageClick(info: ADInfo.Pos1Bean, postion: Int, imageView: View) {
        Log.i("onImageClick====", "position=====" + postion)
        if (httpRequest != null) {
            val token = SharedPerManager.getToken()
            httpRequest!!.AdvClickCount(token, "0", info.id, object : HttpRequestListener {
                override fun requestSuccess(jsonRequest: String) {

                }

                override fun requestFailed(error: String) {

                }

            })
        }
    }
}
