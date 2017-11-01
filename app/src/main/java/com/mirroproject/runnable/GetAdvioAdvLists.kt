package com.mirroproject.runnable

import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.http.RxJavaHelp
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.HashMap

/**
 * Created by reeman on 2017/10/31.
 */
class GetAdvioAdvLists : Runnable {

    override fun run() {
        Flowable.create<Any>({ e ->
            val file = File(AppInfo.VIDEO_ADV_SAVE_DIR)
            if (!file.exists() && file.isDirectory()) {
                file.mkdirs()
            }
            val files = file.listFiles()
            if (files == null || files!!.size == 0) {
                e.setDisposable(object : Disposable {
                    override fun dispose() {}

                    override fun isDisposed(): Boolean {
                        return true
                    }
                })
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).map<Array<File>> { s ->
            val file = File(AppInfo.VIDEO_ADV_SAVE_DIR)
            val files = file.listFiles()
            files
        }.compose(RxJavaHelp.setIoThread()).subscribe({ files ->
            val advs = HashMap<String, String>()
            for (file1 in files) {
                if (file1.getAbsolutePath().endsWith(".mp4")) {
                    val key = file1.getName().replace(".mp4", "").trim({ it <= ' ' })
                    advs.put(key, file1.getAbsolutePath())
                }
            }
            MirrorApplication.getInstance().setVideo_advs(advs)
        })

    }
}
