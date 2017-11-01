package com.mirroproject.http

/**
 * Created by reeman on 2017/10/31.
 */
interface LoginStateListener {

    fun loginSuccess()

    fun loginFailed(failed: String)

    fun loginError(error: String)

    fun parsenError(error: String)

}
