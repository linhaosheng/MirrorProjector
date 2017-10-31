package com.mirroproject.http

import com.mirroproject.view.WaitDialogUtil
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by reeman on 2017/10/31.
 */
class RxJavaHelp {
    companion object {
        fun <T> setThread(): FlowableTransformer<T, T> {
            return FlowableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> setIoThread(): FlowableTransformer<T, T> {
            return FlowableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
            }
        }

        fun <T> setThreadWithDialog(waitDialogUtil: WaitDialogUtil, text: String): FlowableTransformer<T, T> {
            return FlowableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .doOnSubscribe({ subscription -> waitDialogUtil.show(text) })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doFinally({ waitDialogUtil.dismiss() })
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}