package com.mirroproject.app

import com.mirroproject.activity.LoginActivity
import com.mirroproject.fragment.*
import com.mirroproject.http.RetrofitFactory
import com.mirroproject.service.MirrorService
import dagger.Component
import javax.inject.Singleton

/**
 * Created by reeman on 2017/11/1.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(bodyFragment: BodyFragment)

    fun inject(carTonFragment: CarTonFragment)

    fun inject(fingerFragment: FingerFragment)

    fun inject(hairDbFragment: HairDbFragment)

    fun inject(hairFragment: HairFragment)

    fun inject(liveFragment: LiveFragment)

    fun inject(outsideFragment: OutsideFragment)

    fun inject(screenFragment: ScreenFragment)

    fun inject(tvFragment: TVFragment)

    fun inject(mirrorService: MirrorService)

    fun inject(loginActivity: LoginActivity)

    fun addRetrofitFactory(): RetrofitFactory
}
