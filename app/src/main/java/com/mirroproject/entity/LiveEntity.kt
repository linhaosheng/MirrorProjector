package com.mirroproject.entity

import java.io.Serializable

/**
 * Created by reeman on 2017/10/31.
 */
data class LiveEntity(var liveName: String, var imgeUrl: String, var downLoadUrl: String, var packageName: String, var launchName: String) :Serializable{}