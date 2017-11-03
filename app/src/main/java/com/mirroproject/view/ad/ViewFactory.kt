package com.mirroproject.view.ad

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mirroproject.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by reeman on 2017/11/3.
 */
/**
 * ImageView创建工厂
 */
object ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param
     * @return
     */
    fun getImageView(context: Context, url: String): ImageView {
        val imageView = LayoutInflater.from(context).inflate(
                R.layout.view_banner, null) as ImageView
        Glide.with(context).load(url).error(R.drawable.bg_home_ad_error_replace).bitmapTransform(RoundedCornersTransformation(context, 20, 0, RoundedCornersTransformation.CornerType.ALL)).into(imageView)
        return imageView
    }
}
