package com.mirroproject.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.BitmapImageViewTarget

/**
 * Created by reeman on 2017/10/31.
 */

/**
 * 为了解决Glide图片centerCrop()计算错误,导致图片显示变形,在不同的情况下把ImageView设置不同的ScaleType
 */
class MyBitmapImageViewTarget(view: ImageView) : BitmapImageViewTarget(view) {

    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
        if (glideAnimation != null && view.scaleType != ImageView.ScaleType.FIT_XY) {
            view.scaleType = ImageView.ScaleType.FIT_XY
        }
        super.onResourceReady(resource, glideAnimation)
    }

    override fun setResource(resource: Bitmap) {
        super.setResource(resource)
    }

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        if (errorDrawable != null && view != null && view.scaleType != ImageView.ScaleType.CENTER_CROP) {
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        super.onLoadFailed(e, errorDrawable)
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        if (placeholder != null && placeholder != null && view != null && view.scaleType != ImageView.ScaleType.CENTER_CROP) {
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        super.onLoadStarted(placeholder)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        if (placeholder != null && placeholder != null && view != null && view.scaleType != ImageView.ScaleType.CENTER_CROP) {
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        super.onLoadCleared(placeholder)
    }

}
