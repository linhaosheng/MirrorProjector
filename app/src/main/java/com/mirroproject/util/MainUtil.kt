package com.mirroproject.util

import com.mirroproject.R
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.MainEntity
import java.util.*

/**
 * Created by reeman on 2017/11/3.
 */
class MainUtil {

    //电视直播接口
    //直播
    companion object {

        fun getMainList1(): List<MainEntity> {
            val lists: ArrayList<MainEntity> = ArrayList<MainEntity>()
            lists.add(MainEntity("美发", R.mipmap.main_icon_hair, AppInfo.CODE_HAIR, true))
            lists.add(MainEntity("美甲", R.mipmap.main_icon_beautiful, AppInfo.CODE_FINGER, false))
            lists.add(MainEntity("美体", R.mipmap.main_icon_body, AppInfo.CODE_BODY, false))
            lists.add(MainEntity("电视", R.mipmap.main_icon_tv, AppInfo.CODE_MOVIE, false))

            lists.add(MainEntity("卡通", R.mipmap.main_icon_cartong, AppInfo.CODE_DEFAULT, false))
            lists.add(MainEntity("外接", R.mipmap.waijie, AppInfo.CODE_ANIM, false))
            lists.add(MainEntity("直播", R.mipmap.main_icon_live, AppInfo.CODE_DEFAULT, false))
            lists.add(MainEntity("投屏", R.mipmap.main_icon_screen, AppInfo.CODE_DEFAULT, false))
            return lists
        }
    }
}