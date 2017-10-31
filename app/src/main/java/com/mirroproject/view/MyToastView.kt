package com.mirroproject.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mirroproject.R

/**
 * Created by reeman on 2017/10/30.
 */
class MyToastView {


    /**
     * 调用方法 MyToast.getInstence.toast(this,info);
     *
     * @param context
     * @param info
     */
    internal var toast: Toast? = null

    fun Toast(context: Context, info: String) {
        Toast(context, info, Toast.LENGTH_SHORT)
    }

    init {
        if (instance == null) {
            instance = MyToastView()
        }
    }

    companion object {
        lateinit var instance: MyToastView
        fun getInstances(): MyToastView {
            if (instance == null) {
                instance = MyToastView()
            }
            return instance
        }
    }

    fun Toast(context: Context, info: String?, time: Int) {
        if (toast != null) {
            toast!!.cancel()
        }
        val layout = LayoutInflater.from(context).inflate(R.layout.mytoast_view, null)
        val text = layout.findViewById<TextView>(R.id.text)
        if (info == null || info.length < 2) {
            text.text = "   "
        }
        text.text = info
        toast = Toast.makeText(context, "自定义位置Toast", time)
        //        toast.setGravity(Gravity.CENTER, 0, 0);
        toast!!.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 150)
        toast!!.view = layout
        toast!!.show()
    }

}