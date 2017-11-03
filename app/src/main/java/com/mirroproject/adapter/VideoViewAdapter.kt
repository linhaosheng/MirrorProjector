package com.mirroproject.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mirroproject.app.MirrorApplication
import com.mirroproject.entity.VideoEntity
import com.mirroproject.R.id.iv_video_pic
import com.mirroproject.R.id.tv_video_desc
import com.mirroproject.R.id.scfragment
import com.mirroproject.view.recycleview.SWFrameLayout
import com.mirroproject.R.color.colorHomeListTextUnfocused
import android.view.MotionEvent
import com.mirroproject.R.color.colorHomeListTextFocused
import android.view.View.OnHoverListener
import com.mirroproject.util.MyLog
import android.view.View.OnFocusChangeListener
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.mirroproject.R.layout.item_layout_video
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mirroproject.R
import com.mirroproject.view.MyBitmapImageViewTarget


/**
 * Created by reeman on 2017/10/31.
 */
class VideoViewAdapter(private val mContext: Context, private var data: List<VideoEntity.DataBean>) : RecyclerView.Adapter<VideoViewAdapter.Holder>() {

    lateinit var mOnItemClickListener: OnItemClickListener
    lateinit var mOnItemKeyListener: OnItemKeyListener

    companion object {
        val TAG = VideoViewAdapter::class.java.simpleName
    }

    fun setList(data: List<VideoEntity.DataBean>) {
        this.data = data
    }

    fun setData(data: List<VideoEntity.DataBean>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_video, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val entity = data[position]
        holder.tv_video_desc.setText(entity.title)
        holder.itemView.isFocusable = true
        holder.itemView.tag = position

        Glide.with(mContext)
                .load(entity.picpath)
                .asBitmap()
                .centerCrop()
                .into(MyBitmapImageViewTarget(holder.iv_video_pic))

        var params: FrameLayout.LayoutParams? = null
        //         width,  height,  left,  top,  right,  bottom
        params = setLayoutSize(185, 330, 10, 0, 10, 0)
        holder.scfragment.layoutParams = params

        if (mOnItemKeyListener != null) {
            holder.itemView.setOnKeyListener({ v, keyCode, event ->
                val onKey = mOnItemKeyListener!!.onKey(v, keyCode, event)
                return@setOnKeyListener onKey
            })
        }
        Log.i(TAG, "========fource")

        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                MyLog.i("position", "当前获取焦点的位置==" + position)
                ViewCompat.animate(v).scaleX(1.1f).scaleY(1.1f).translationZ(1f).setDuration(500).start()
                holder.tv_video_desc.setTextColor(ContextCompat.getColor(
                        mContext, R.color.colorHomeListTextFocused))
            } else {
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(500).start()
                holder.tv_video_desc.setTextColor(ContextCompat.getColor(
                        mContext, R.color.colorHomeListTextUnfocused))
            }
            Log.i(TAG, "has focus:$position--$hasFocus")
        }
        holder.itemView.setOnHoverListener { view, motionEvent ->
            val action = motionEvent.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
                -> {
                    ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1f).setDuration(500).start()
                    holder.tv_video_desc.setTextColor(ContextCompat.getColor(
                            mContext, R.color.colorHomeListTextFocused))
                }
                MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
                -> {
                    ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(500).start()
                    holder.tv_video_desc.setTextColor(ContextCompat.getColor(
                            mContext, R.color.colorHomeListTextUnfocused))
                }
            }
            return@setOnHoverListener false
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { view -> mOnItemClickListener.onItemClick(view, entity) }
        }
    }

    private fun setLayoutSize(width: Int, height: Int, left: Int, top: Int, right: Int, bottom: Int): FrameLayout.LayoutParams {
        val params: FrameLayout.LayoutParams
        params = FrameLayout.LayoutParams(width, height)
        params.setMargins(left, top, right, bottom)
        return params
    }


    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_video_desc: TextView
        var iv_video_pic: ImageView
        var scfragment: SWFrameLayout

        init {
            scfragment = view.findViewById<View>(R.id.scfragment) as SWFrameLayout
            tv_video_desc = view.findViewById<View>(R.id.tv_video_desc) as TextView
            iv_video_pic = view.findViewById<View>(R.id.iv_video_pic) as ImageView
        }
    }

    /*设置item点击事件的接口*/
    interface OnItemClickListener {
        fun onItemClick(view: View, entity: VideoEntity.DataBean)
    }

    /*设置item的onKey事件的接口*/
    interface OnItemKeyListener {
        fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemKeyListener(listener: OnItemKeyListener) {
        mOnItemKeyListener = listener
    }

}