package com.mirroproject.activity.splash

import com.mirroproject.app.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by reeman on 2017/11/1.
 */
@Singleton
@Component(modules = arrayOf(SplashActivityModule::class, AppModule::class))
interface SplashActivityComponent {
    fun inject(splashActivityHelp: SplashActivityHelp)

    fun inject(splashActivity: SplashActivity)
}