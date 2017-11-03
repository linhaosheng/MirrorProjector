package com.mirroproject.view

import android.view.animation.Interpolator

/**
 * Created by reeman on 2017/10/31.
 */
class DampingInterpolator @JvmOverloads constructor(private val mCycles: Float = 1f) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return (Math.sin(mCycles.toDouble() * 2.0 * Math.PI * input.toDouble()) * ((input - 1) * (input - 1))).toFloat()
    }
}
