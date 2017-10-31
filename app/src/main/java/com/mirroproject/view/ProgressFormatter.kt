package com.mirroproject.view

/**
 * Created by reeman on 2017/10/31.
 */
interface ProgressFormatter {
    abstract fun getFormattedText(progress: Int, max: Int): String
}