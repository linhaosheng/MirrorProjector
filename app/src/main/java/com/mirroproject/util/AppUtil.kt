package com.mirroproject.util

import android.content.Context
import android.media.AudioManager
import com.mirroproject.app.MirrorApplication

/**
 * Created by reeman on 2017/11/6.
 */
class AppUtil {

    companion object {
        fun getCurrentVolum(): Int {
            val am = MirrorApplication.getInstance().getContext()!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return am.getStreamVolume(AudioManager.STREAM_MUSIC)
        }

        fun setVolum(volum: Int) {
            val am = MirrorApplication.getInstance().getContext()!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volum, 0)
        }
    }
}
