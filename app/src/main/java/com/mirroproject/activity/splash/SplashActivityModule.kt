package com.mirroproject.activity.splash

import com.mirroproject.util.PlayerUtil
import dagger.Module
import dagger.Provides
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.logging.Handler

/**
 * Created by reeman on 2017/10/31.
 */
@Module
class SplashActivityModule {

    var splashActivity: SplashActivity
    var handler: Handler

    constructor(splashActivity: SplashActivity, handler: Handler) {
        this.splashActivity = splashActivity
        this.handler = handler
    }

    @Provides
    fun providerPlayUtil(): PlayerUtil {
        return PlayerUtil(splashActivity, splashActivity.surfaceView1, splashActivity.skbProgress)
    }
}