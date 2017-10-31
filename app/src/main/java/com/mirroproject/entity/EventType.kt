package com.mirroproject.entity

/**
 * Created by reeman on 2017/10/31.
 */
class EventType {

    class CycleEventType(var cycleType: Int)

    class PlayEventType(var palyType: Int)

    class SetVideoLayoutType(var videoType: Int)

    class LoginType(var loginType: Int) {
        companion object {
            val LOGIN_SUCCESS = 1
            val LOGIN_ERROR = 2
        }
    }

    class ExitType(var exitType: Int)

    class CloseScreenType(var closeType: Int)

    class FirewareTypeEvent(var firewareType: Int)

    class UpdateEventType(var updateType: Int)

    class DownLoadTypeEvent(var downLoadType: Int)

    class StopDownLoadTypeEvent(var stopDownLoadType: Int)

    class DownLoadSpeed(var downloadSpeed: Long)

    class DownLoadAdvSize(var downloadAdvSize: Long)
}
