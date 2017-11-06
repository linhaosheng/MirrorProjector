package com.mirroproject.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.mirroproject.R
import com.mirroproject.adapter.ExitAdapter
import com.mirroproject.app.MirrorApplication
import com.mirroproject.config.AppInfo
import com.mirroproject.entity.EventType
import com.mirroproject.entity.ExitEntity
import com.mirroproject.util.AppUtil
import com.mirroproject.util.CodeUtil
import com.mirroproject.util.SharedPerManager
import com.mirroproject.view.DialogConfirm
import com.mirroproject.view.DialogMessage
import com.mirroproject.view.MyToastView
import com.mirroproject.view.SystemDialog
import kotlinx.android.synthetic.main.activity_exit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.ArrayList

/**
 * Created by reeman on 2017/11/3.
 */
class ExitActivity : BaseActivity(), View.OnClickListener, DialogMessage.DialogMessageListener, AdapterView.OnItemClickListener {


    private var dialogConfirm: DialogConfirm? = null
    internal var SAVE_EXIT_CODE = AppInfo.EXIT_PWD  //786496
    internal var lists: MutableList<ExitEntity> = ArrayList<ExitEntity>()
    private var systemDialog: SystemDialog? = null
    private var dialogMessage: DialogMessage? = null
    lateinit var adapter: ExitAdapter


    var itemSelectedListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>, view: View, point: Int, l: Long) {
            Log.i(TAG, "=====当前点击的控件==" + point)
            for (j in lists.indices) {
                if (j == point) {
                    lists[j].isCheck = true
                } else {
                    lists[j].isCheck = false
                }
            }
            adapter.setList(lists)
            adapter.notifyDataSetChanged()
            for (k in lists.indices) {
                Log.i(TAG, "=========" + lists[k].toString())
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>) {

        }
    }

    internal var menu_click = 0

    val password: String
        get() = et_password!!.text.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit)
        ButterKnife.bind(this)
        EventBus.getDefault().register(this)
        getListData()
        initView()
    }

    private fun getListData() {
        lists.add(ExitEntity(R.drawable.ic_reboot_sys, "重启", false))
        lists.add(ExitEntity(R.drawable.ic_exit_login, "退出登录", false))
        lists.add(ExitEntity(R.drawable.ic_cache_clean, "清理缓存", false))
        lists.add(ExitEntity(R.drawable.camer2, "拍照", false))
        lists.add(ExitEntity(R.drawable.icon_update, "升级", false))
        lists.add(ExitEntity(R.mipmap.icon_bleeth, "蓝牙设定", false))
        lists.add(ExitEntity(R.drawable.icon_wifi, "网络设定", false))
    }


    private fun initView() {
        adapter = ExitAdapter(this@ExitActivity, lists)
        grid_exit!!.adapter = adapter
        grid_exit!!.onItemClickListener = this
        grid_exit!!.onItemSelectedListener = itemSelectedListener
        dialogMessage = DialogMessage(this)
        dialogMessage!!.setOnDialogClickListener(this)
        dialogMessage!!.setCurrentProgress(SharedPerManager.getMaxVolumn())
        systemDialog = SystemDialog(this)
        dialogConfirm = DialogConfirm(this)
        tv_version!!.text = "版本 ：" + CodeUtil.getVersion(this@ExitActivity)
        et_password!!.onFocusChangeListener = View.OnFocusChangeListener { view, b -> showKeyBord(et_password, b) }
        btn_exit!!.setOnClickListener(this)
        btn_cancel!!.setOnClickListener(this)
    }


    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        when (i) {
            0 -> if (!dialogConfirm!!.isShow) {
                dialogConfirm!!.show("确定要重启 ?", 0)
                exitConfirm()
            }
            1 -> {
                if (password != SAVE_EXIT_CODE) {
                    MyToastView.getInstances().Toast(this@ExitActivity, "请先输入密码")
                    return
                }
                if (!dialogConfirm!!.isShow) {
                    dialogConfirm!!.show("确定要退出登录 ?", EXIT_LOGIN)
                    exitConfirm()
                }
            }
            2 -> if (!dialogConfirm!!.isShow) {
                dialogConfirm!!.show("是否删除缓存 ?", CLEAR_CACHE)
                exitConfirm()
            }
            4 -> startActivity(Intent(this@ExitActivity, UpdateActiivty::class.java))  //升级
            5 -> startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))  //蓝牙设置
            6 -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) //直接进入手机中的wifi网络设置界面
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> finish()
            R.id.btn_exit -> if (password == SAVE_EXIT_CODE) {
                et_password!!.setText("")
                startActivity(Intent(this@ExitActivity, LauncherActivity::class.java))
            } else {
                MyToastView.getInstances().Toast(this, "密码错误")
            }
        }
    }

    fun exitConfirm() {
        dialogConfirm!!.setTitle("提醒!")
        dialogConfirm!!.hideSpeed()
        dialogConfirm!!.setBtnText("确定", "取消")
    }

    fun showKeyBord(v: View?, isShow: Boolean) {
        val imm = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            et_password!!.inputType = EditorInfo.TYPE_CLASS_PHONE
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED)
        } else {
            imm.hideSoftInputFromWindow(v.windowToken, 0) //强制隐藏键盘
        }
    }

    override fun onResume() {
        super.onResume()
        val bitmap = BitmapFactory.decodeFile(AppInfo.ER_CODE_PATH)
        if (bitmap != null) {
            monitor_er_code!!.setText(CodeUtil.getBlueToothCode())
            iv_qr_code!!.setImageBitmap(bitmap)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun exit(exitType: EventType.ExitType) {
        when (exitType.exitType) {
            CLEAR_CACHE -> {
                val file = File(AppInfo.VIDEO_ADV_SAVE_DIR)
                if (file.exists()) {
                    val files = file.listFiles()
                    for (file1 in files!!) {
                        val delete = file1.delete()
                        Log.i("delete======", delete.toString())
                    }
                }
                MirrorApplication.getInstance().clearCache()
            }
            0 -> {
                val intent = Intent(Intent.ACTION_REBOOT)
                intent.putExtra("nowait", 1)
                intent.putExtra("interval", 1)
                intent.putExtra("window", 0)
                sendBroadcast(intent)
            }
            EXIT_LOGIN -> {
                SharedPerManager.setUserName("")
                SharedPerManager.setPassword("")
                startActivity(Intent(this@ExitActivity, LoginActivity::class.java))
                finish()
            }
        }
        if (dialogConfirm != null) {
            dialogConfirm!!.dimiss()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            for (i in lists.indices) {
                lists[i].isCheck = false
            }
            adapter.setList(lists)
            adapter.notifyDataSetChanged()
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            for (i in lists.indices) {
                lists[i].isCheck = false
            }
            adapter.setList(lists)
            adapter.notifyDataSetChanged()
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu_click++
            if (menu_click > 5) {
                startActivity(Intent(this@ExitActivity, LauncherActivity::class.java))
                menu_click = 0
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun noSure() {

    }

    override fun sure(maxVolum: Int) {
        AppUtil.setVolum(maxVolum)
        dialogMessage!!.dissmiss()
        SharedPerManager.setMaxVolum(maxVolum)
    }

    companion object {

        private val TAG = "ExitActivity==="


        val EXIT_LOGIN = 125648
        val CLEAR_CACHE = EXIT_LOGIN + 1
    }
}
