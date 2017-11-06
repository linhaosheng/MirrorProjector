package com.mirroproject.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mirroproject.R
import com.mirroproject.entity.ExitEntity

/**
 * Created by reeman on 2017/11/6.
 */

class ExitAdapter(internal var context: Context, internal var lists: List<ExitEntity>) : BaseAdapter() {
    internal var inflater: LayoutInflater


    fun setList(lists: List<ExitEntity>) {
        this.lists = lists
    }

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
            view = inflater.inflate(R.layout.item_exit, null)
            holder.iv_launcher_item = view!!.findViewById<View>(R.id.iv_exit_icon) as ImageView
            holder.rela_item_bgg = view.findViewById<View>(R.id.rela_item_bgg) as LinearLayout
            holder.tv_exit_desc = view.findViewById<View>(R.id.tv_exit_desc) as TextView
            view.tag = holder
        } else {
            view = convertview
            holder = view.tag as ViewHolder
        }
        val entity = lists[position]
        holder.tv_exit_desc!!.setText(entity.desc)
        holder.iv_launcher_item!!.setBackgroundResource(entity.imageId)
        val exitEntity = lists[position]
        Log.i("adapter", "=====entity==" + exitEntity.toString())
        if (exitEntity.isCheck) {
            holder.rela_item_bgg!!.setBackgroundResource(R.drawable.rect_circle_blue)
        } else {
            holder.rela_item_bgg!!.setBackgroundResource(R.drawable.rect_circle_tranclate)
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
        return view
    }

    inner class ViewHolder {
        internal var rela_item_bgg: LinearLayout? = null
        internal var iv_launcher_item: ImageView? = null
        internal var tv_exit_desc: TextView? = null
    }
}
