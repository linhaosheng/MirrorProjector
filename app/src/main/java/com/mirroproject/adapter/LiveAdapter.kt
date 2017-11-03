package com.mirroproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.mirroproject.R
import com.mirroproject.entity.LiveEntity
import com.mirroproject.util.ImageUtil

/**
 * Created by reeman on 2017/11/3.
 */

class LiveAdapter(private val context: Context) : BaseAdapter() {

    private var liveEntityList: List<LiveEntity>? = null
    private var noSelect: Boolean = false

    fun setSelector(noSelect: Boolean) {
        this.noSelect = noSelect
    }

    fun setLiveEntityList(entityList: List<LiveEntity>) {
        this.liveEntityList = entityList
    }

    override fun getCount(): Int {
        return if (liveEntityList != null) liveEntityList!!.size else 0
    }

    override fun getItem(position: Int): Any {
        return liveEntityList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = null
        var viewHodle: ViewHodle? = null
        if (convertView == null) {
            viewHodle = ViewHodle()
            view = LayoutInflater.from(context).inflate(R.layout.item_live_grid, null)
            viewHodle.lin_layout_bgg = view!!.findViewById<View>(R.id.lin_layout_bgg) as RelativeLayout
            viewHodle.name = view.findViewById<View>(R.id.liveName) as TextView
            viewHodle.pic = view.findViewById<View>(R.id.liveImg) as ImageView
            view.tag = viewHodle
        } else {
            view = convertView
            viewHodle = convertView.tag as ViewHodle
        }
        if (noSelect) {
            view.setBackgroundResource(R.drawable.rect_circle_tranclate)
        }
        viewHodle.lin_layout_bgg!!.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                //                    ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1).setDuration(500).start();
                view.setBackgroundResource(R.drawable.rect_circle_bordle5)
            } else {
                //                    ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1).setDuration(500).start();
                view.setBackgroundResource(R.drawable.rect_circle_tranclate)
            }
        }
        viewHodle.lin_layout_bgg!!.setOnHoverListener { view, motionEvent ->
            val action = motionEvent.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
                ->
                    //                        ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1).setDuration(500).start();
                    view.setBackgroundResource(R.drawable.rect_circle_bordle5)
                MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
                ->
                    //                        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1).setDuration(500).start();
                    view.setBackgroundResource(R.drawable.rect_circle_tranclate)
            }
            false
        }

        val liveEntity = liveEntityList!![position]
        viewHodle.name!!.setText(liveEntity.liveName)
        ImageUtil.loadImg(context, viewHodle.pic!!, liveEntity.imgeUrl)
        return view
    }

    private inner class ViewHodle {
        internal var lin_layout_bgg: RelativeLayout? = null
        var name: TextView? = null
        var pic: ImageView? = null
    }
}
