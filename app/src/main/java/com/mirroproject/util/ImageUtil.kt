package com.mirroproject.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by reeman on 2017/11/3.
 */
class ImageUtil {
    companion object {
        fun loadImg(context: Context, imageView: ImageView, url: String) {
            Glide.with(context).load(url).into(imageView)
        }
    }
}
