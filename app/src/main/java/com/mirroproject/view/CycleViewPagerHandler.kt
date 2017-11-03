package com.mirroproject.view

import android.content.Context
import android.os.Handler

/**
 * Created by reeman on 2017/11/3.
 */
/**
 * 为了防止内存泄漏，定义外部类，防止内部类对外部类的引用
 */
class CycleViewPagerHandler(var context: Context) : Handler(){}