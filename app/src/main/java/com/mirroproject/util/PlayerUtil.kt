package com.mirroproject.util

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.SeekBar
import com.mirroproject.app.MirrorApplication
import com.mirroproject.entity.EventType
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by reeman on 2017/10/31.
 */
class PlayerUtil : MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback, MediaPlayer.OnErrorListener {

    var videoWidth: Int = 0
    var videoHeight: Int = 0
    lateinit var mediaPlayer: MediaPlayer
    lateinit var surfaceHolder: SurfaceHolder
    internal var skbProgress: SeekBar? = null
    private val mTimer = Timer()
    val TAG = "mediaPlayer"
    var cacheProgress = 0
    lateinit var context: Context
    //  WaitDialogUtil waitDialogUtil;
    internal var waitDialogUtil: WaitDialogVideoUtil? = null
    private var isLoaclVideo = false
    private val SET_VIDEO_LAYOUT = 1


    constructor(context: Context, surfaceView: SurfaceView, skbProgress: SeekBar) {
        this.context = context
        this.skbProgress = skbProgress
        waitDialogUtil = WaitDialogVideoUtil(context)
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mTimer.schedule(mTimerTask, 0, 500)
    }


    internal var mTimerTask: TimerTask? = object : TimerTask() {
        override fun run() {
            if (mediaPlayer == null) {
                return
            }
            if (mediaPlayer!!.isPlaying && skbProgress!!.isPressed == false) {
                handleProgress.sendEmptyMessage(UPDATE_PROGRESS)
            }
        }
    }


    private val UPDATE_PROGRESS = 0
    internal var handleProgress: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UPDATE_PROGRESS -> {
                    val position = mediaPlayer!!.currentPosition
                    val duration = mediaPlayer!!.duration
                    if (duration > 0) {
                        val pos = (skbProgress!!.max * position / duration).toLong()
                        skbProgress!!.progress = pos.toInt()
                        if (!isLoaclVideo) {
                            updateDialog(pos)
                        }
                    }
                }
                SET_VIDEO_LAYOUT -> EventBus.getDefault().post(EventType.SetVideoLayoutType(1))
            }
        }
    }


    private fun updateDialog(pos: Long) {
        val currentPotion = mediaPlayer!!.currentPosition
        Log.i("mediaPlayer", "==播放进度==$currentPotion  ///缓冲进度===$cacheProgress")
        if (cacheProgress - currentPotion <= 1000) {
            if (pos >= 98) {
                waitDialogUtil!!.dismiss()
                return
            }
            if (!waitDialogUtil!!.isShowing()) {
                waitDialogUtil!!.show("玩命加载中...")
            }
        } else {
            waitDialogUtil!!.dismiss()
        }
        Log.i(TAG, "==当前播放进度==$pos//缓冲进度==$cacheProgress")
    }

    fun hideDialog() {
        if (waitDialogUtil != null && waitDialogUtil!!.isShowing()) {
            waitDialogUtil!!.dismiss()
        }
    }

    fun playUrl(videoUrl: String) {
        try {
            if (!waitDialogUtil!!.isShowing()) {
                waitDialogUtil!!.show("玩命加载中...")
            }
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(videoUrl)
            mediaPlayer!!.prepare()//prepare之后自动播放
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun playUrlHide(videoUrl: Uri) {
        try {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(context, videoUrl)
            mediaPlayer!!.prepare()//prepare之后自动播放
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun playUrlHide(videoUrl: String) {
        try {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(videoUrl)
            mediaPlayer!!.prepare()//prepare之后自动播放
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //关闭声音
    fun closeSound() {
        if (mediaPlayer != null) {
            //mediaPlayer.setVolume(0, 0);
            val audioManager = MirrorApplication.getInstance().getContext()!!.getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
        }
    }

    fun openSound() {
        if (mediaPlayer != null) {
            val audioManager = MirrorApplication.getInstance().getContext()!!.getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
            /* mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            mediaPlayer.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));*/
        }
    }

    fun isPlaying(): Boolean {
        return if (mediaPlayer == null) {
            false
        } else mediaPlayer!!.isPlaying
    }

    fun isShowing(): Boolean {
        return if (waitDialogUtil != null) {
            waitDialogUtil!!.isShowing()
        } else false
    }

    fun getCurrentPoint(): Int {
        return mediaPlayer!!.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    fun setLoaclVideo(loaclVideo: Boolean) {
        this.isLoaclVideo = loaclVideo
    }

    fun play() {
        if (mediaPlayer == null) {
            return
        }
        mediaPlayer!!.start()
    }

    fun pause() {
        if (mediaPlayer == null) {
            return
        }
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
    }

    fun stop() {
        try {
            if (mediaPlayer != null) {
                if (mTimerTask != null) {
                    mTimerTask!!.cancel()
                    mTimerTask = null
                }
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                }
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }

    }

    override fun surfaceChanged(arg0: SurfaceHolder, arg1: Int, arg2: Int, arg3: Int) {
        Log.e(TAG, "===========surface changed")
    }

    override fun surfaceCreated(arg0: SurfaceHolder) {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDisplay(surfaceHolder)
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.setOnErrorListener(this)
        } catch (e: Exception) {
            Log.e(TAG, "error", e)
        }

        Log.e(TAG, "============surface created")
    }

    override fun surfaceDestroyed(arg0: SurfaceHolder) {
        Log.e("mediaPlayer", "surface destroyed")
    }

    /**
     * 通过onPrepared播放
     */
    override fun onPrepared(arg0: MediaPlayer) {
        videoWidth = mediaPlayer!!.videoWidth
        videoHeight = mediaPlayer!!.videoHeight
        if (videoHeight == 0 || videoWidth == 0) {
            handleProgress.sendEmptyMessageDelayed(SET_VIDEO_LAYOUT, 2000)
        }
        EventBus.getDefault().post(EventType.PlayEventType(3))
        arg0.start()
        Log.e("mediaPlayer", "onPrepared")
    }

    fun getMediaPlayer1(): MediaPlayer? {
        return mediaPlayer
    }

    override fun onCompletion(arg0: MediaPlayer) {
        Log.e("mediaPlayer", "视频播放完毕了")
        EventBus.getDefault().post(EventType.PlayEventType(2))
    }


    override fun onBufferingUpdate(arg0: MediaPlayer, bufferingProgress: Int) {
        skbProgress!!.secondaryProgress = bufferingProgress
        val duration = mediaPlayer!!.duration
        cacheProgress = duration * bufferingProgress / 100
    }


    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        Log.e("mediaPlayer", "onError===$i====il$i1")
        return false
    }
}