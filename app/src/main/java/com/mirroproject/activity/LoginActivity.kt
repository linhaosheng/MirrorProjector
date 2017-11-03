package com.mirroproject.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mirroproject.R
import com.mirroproject.activity.main.MainActivity
import com.mirroproject.app.AppModule
import com.mirroproject.app.DaggerAppComponent
import com.mirroproject.app.MirrorApplication
import com.mirroproject.entity.EventType
import com.mirroproject.service.MirrorService
import com.mirroproject.service.PopupService
import com.mirroproject.util.NetWorkUtils
import com.mirroproject.util.SharedPerManager
import com.mirroproject.view.MyToastView
import com.mirroproject.view.WaitDialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */
class LoginActivity : BaseActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnHoverListener {

    @Inject
    lateinit var waitDialogUtil: WaitDialogUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        EventBus.getDefault().register(this)
        DaggerAppComponent.builder().appModule(AppModule(this)).build().inject(this)
        initview()
    }

    companion object {
        val LOGIN_SUCCESS = 0
        val LOGIN_FAILED = LOGIN_SUCCESS + 1
    }

    private fun initview() {
        val userName = SharedPerManager.getUserName()
        val password = SharedPerManager.getPassword()
        et_user!!.setText(userName)
        et_password!!.setText(password)
        et_user.onFocusChangeListener = this
        et_password.onFocusChangeListener = this
        btn_login!!.setOnClickListener(this)
        btn_login.onFocusChangeListener = this
        btn_login.setOnHoverListener(this)
    }


    override fun onFocusChange(view: View, b: Boolean) {
        if (b) {
            if (view.id == R.id.et_password || view.id == R.id.et_user) {
                showKeyBord(view, true)
            } else if (view.id == R.id.btn_login) {
                view.setBackgroundResource(R.drawable.rect_circle_blue)
            }
        } else {
            if (view.id == R.id.btn_login) {
                view.setBackgroundResource(R.drawable.rect_circle_grey)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                if (!NetWorkUtils.isNetworkConnected(this@LoginActivity)) {
                    MyToastView.getInstances().Toast(this@LoginActivity, "网络异常，请检查")
                    return
                }
                val username = getUserName()
                val password = getPassword()
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    MyToastView.getInstances().Toast(this@LoginActivity, "请输入正确的用户名名.密码！")
                    return
                }
                if (password.length < 6) {
                    MyToastView.getInstances().Toast(this@LoginActivity, "请输入大于6位数的密码！")
                    return
                }
                MirrorService.getInstance().login(waitDialogUtil, this, getUserName(), getPassword())
            }
        }
    }

    fun showKeyBord(v: View, isShow: Boolean) {
        //        edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED)
        } else {
            imm.hideSoftInputFromWindow(v.windowToken, 0) //强制隐藏键盘
        }
    }

    fun getPassword(): String {
        return et_password!!.text.toString().trim { it <= ' ' }
    }

    fun getUserName(): String {
        return et_user!!.text.toString().trim { it <= ' ' }
    }

    override fun onHover(view: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action
        when (action) {
            MotionEvent.ACTION_HOVER_ENTER//鼠标进入view
            -> view.setBackgroundResource(R.drawable.rect_circle_blue)
            MotionEvent.ACTION_HOVER_EXIT//鼠标离开view
            -> view.setBackgroundResource(R.drawable.rect_circle_grey)
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this@LoginActivity, ExitActivity::class.java))
        }
        return super.onKeyDown(keyCode, event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isLogin(loginType: EventType.LoginType) {
        if (loginType.loginType === 1) {
            MyToastView.getInstances().Toast(this@LoginActivity, "自动登录成功")
            startService(Intent(MirrorApplication.getInstance(), PopupService::class.java))  //GIf悬浮窗界面开始
            startService(Intent(this@LoginActivity, MirrorService::class.java))
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}