package com.mirroproject.activity.main

import com.mirroproject.app.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by reeman on 2017/11/3.
 */
@Singleton
@Component(modules = arrayOf(MainModule::class, AppModule::class))
interface MainComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(mainActivityHelp: MainActivityHelp)
}