package com.mirroproject.app

import android.content.Context
import com.mirroproject.http.HttpRequest
import com.mirroproject.http.RetrofitFactory
import com.mirroproject.http.VideoInfoRetrofitFactory
import com.mirroproject.util.DownManager
import com.mirroproject.util.WaitDialogVideoUtil
import com.mirroproject.view.DialogConfirm
import com.mirroproject.view.SystemDialog
import com.mirroproject.view.WaitDialogUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by reeman on 2017/11/1.
 */
@Module
class AppModule {
    var activity: Context

    constructor(activity: Context) {
        this.activity = activity
    }

    @Provides
    @Singleton
    fun providerHttpRequest(): HttpRequest {
        return HttpRequest()
    }

    @Provides
    fun providerDownManager(): DownManager {
        return DownManager(activity)
    }

    @Provides
    fun providerSystemDialog(): SystemDialog {
        return SystemDialog(activity)
    }

    @Provides
    fun providerDialogConfirm(): DialogConfirm {
        return DialogConfirm(activity)
    }

    @Provides
    fun providerWaitDialogVideoUtil(): WaitDialogVideoUtil {
        return WaitDialogVideoUtil(activity)
    }

    @Provides
    fun providerWaitDialogUtil(): WaitDialogUtil {
        return WaitDialogUtil(activity)
    }

    @Provides
    @Singleton
    fun providerRetrofitFactory(): RetrofitFactory {
        return RetrofitFactory()
    }

    @Provides
    @Singleton
    fun providerVideoInfoRetrofitFactory(): VideoInfoRetrofitFactory {
        return VideoInfoRetrofitFactory()
    }

    @Provides
    @Singleton
    fun provider(): Context {
        return activity
    }
}