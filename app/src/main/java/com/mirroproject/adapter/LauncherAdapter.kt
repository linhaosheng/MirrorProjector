package com.mirroproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mirroproject.R
import com.mirroproject.entity.LaunchEntity

/**
 * Created by reeman on 2017/11/6.
 */

class LauncherAdapter(internal var context: Context, internal var lists: List<LaunchEntity>) : BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return lists.size
    }

    override fun getItem(i: Int): Any {
        return lists[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertview == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.item_launcher, null)
            holder.iv_launcher_item = view!!.findViewById<View>(R.id.iv_launcher_item) as ImageView
            holder.rela_item_bgg = view.findViewById<View>(R.id.rela_item_bgg) as RelativeLayout
            view.tag = holder
        } else {
            view = convertview
            holder = view.tag as ViewHolder
        }
        holder.rela_item_bgg!!.setOnHoverListener { view, motionEvent ->
            val action = motionEvent.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
                -> view.setBackgroundResource(R.drawable.rect_circle_blue)
                MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
                -> view.setBackgroundResource(R.drawable.rect_circle_tranclate)
            }
            false
        }

        val entity = lists[position]
        holder.iv_launcher_item!!.setBackgroundResource(entity.imageId)
        return view
    }

    inner class ViewHolder {
        internal var rela_item_bgg: RelativeLayout? = null
        internal var iv_launcher_item: ImageView? = null
    }
}
