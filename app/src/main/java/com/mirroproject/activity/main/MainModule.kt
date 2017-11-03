package com.mirroproject.activity.main

import android.os.Handler
import dagger.Module
import dagger.Provides

/**
 * Created by reeman on 2017/11/3.
 */
@Module
class MainModule(var mainActivity: MainActivity, var handler: Handler, var mainActivityView: MainActivityView) {

    @Provides
    fun providerMainActivityHelp(): MainActivityHelp {
        return MainActivityHelp(mainActivity, handler, mainActivityView)
    }

    @Provides
    fun providerMainActivityBussin(): MainActivityBussin {
        return MainActivityBussin()
    }


    @Provides
    fun providerMainActivityView(): MainActivityView {
        return mainActivityView
    }

}
