package com.cstec.administrator.chart_module.Base

import android.app.Dialog
import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.cstec.administrator.chart_module.Base.Swipe.SwipeBackActivityHelper
import com.cstec.administrator.chart_module.Base.Swipe.SwipeInteface
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.View.SwipeBackLayout
import com.cstec.administrator.chart_module.Base.Swipe.SwipeUtils
import android.view.inputmethod.InputMethodManager
import cn.jpush.im.android.api.JMessageClient
import android.util.DisplayMetrics
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Button
import android.view.WindowManager
import com.cstec.administrator.chart_module.View.ChatUtils.DialogCreator
import cn.jpush.im.api.BasicCallback
import com.cstec.administrator.chart_module.View.SharePreferenceManager
import com.cstec.administrator.chart_module.View.ChatUtils.FileHelper
import cn.jpush.im.android.api.event.LoginStateChangeEvent
import com.cstec.administrator.chart_module.R


abstract class ChartBaseActivity<V : ViewDataBinding, VM : BaseViewModel> : BaseActivity<V, VM>(), SwipeInteface {
    protected var mWidth: Int = 0
    protected var mHeight: Int = 0
    protected var mDensity: Float = 0.toFloat()
    protected var mDensityDpi: Int = 0
    private var mJmui_title_tv: TextView? = null
    private var mReturn_btn: ImageButton? = null
    private var mJmui_title_left: TextView? = null
    var mJmui_commit_btn: Button? = null
    protected var mAvatarSize: Int = 0
    protected var mRatio: Float = 0.toFloat()
    var chat_dialog: Dialog? = null
    lateinit var mHelper: SwipeBackActivityHelper
    override fun initData() {
        super.initData()
        JMessageClient.registerEventReceiver(this)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mDensity = dm.density
        mDensityDpi = dm.densityDpi
        mWidth = dm.widthPixels
        mHeight = dm.heightPixels
        mRatio = Math.min(mWidth as Float / 720, mHeight as Float / 1280)
        mAvatarSize = (50 * mDensity) as Int
        mHelper = SwipeBackActivityHelper(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper.onPostCreate()
    }

    override fun <T : View> findViewById(id: Int): T {
        val v = super.findViewById<View>(id)
        return if (v == null && mHelper != null) mHelper.findViewById(id) as T else v as T
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper.getSwipeBackLayout()!!
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        getSwipeBackLayout().setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        SwipeUtils.convertActivityToTranslucent(this)
        getSwipeBackLayout().scrollToFinishActivity()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null != this.currentFocus) {
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager!!.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.onTouchEvent(event)
    }

    fun onEventMainThread(event: LoginStateChangeEvent) {
        val reason = event.reason
        val myInfo = event.myInfo
        if (myInfo != null) {
            val path: String
            val avatar = myInfo.avatarFile
            if (avatar != null && avatar.exists()) {
                path = avatar.absolutePath
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.userName)
            }
            SharePreferenceManager.setCachedUsername(myInfo.userName)
            SharePreferenceManager.setCachedAvatarPath(path)
            JMessageClient.logout()
        }
        when (reason) {
            LoginStateChangeEvent.Reason.user_logout -> {
                val listener = View.OnClickListener { v ->
                    when (v.id) {
                        R.id.jmui_cancel_btn -> {
//                            val intent = Intent(this@BaseActivity, LoginActivity::class.java)
//                            startActivity(intent)
                        }
                        R.id.jmui_commit_btn -> JMessageClient.login(SharePreferenceManager.getCachedUsername(), SharePreferenceManager.getCachedPsw(), object : BasicCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String) {
                                if (responseCode == 0) {
//                                    val intent = Intent(this@BaseActivity, MainActivity::class.java)
//                                    startActivity(intent)
                                }
                            }
                        })
                    }
                }
                chat_dialog = DialogCreator.createLogoutStatusDialog(this@ChartBaseActivity, "您的账号在其他设备上登陆", listener)
                chat_dialog!!.window!!.setLayout((0.8 * mWidth) as Int, WindowManager.LayoutParams.WRAP_CONTENT)
                chat_dialog!!.setCanceledOnTouchOutside(false)
                chat_dialog!!.show()
            }
            LoginStateChangeEvent.Reason.user_password_change -> {
//                val intent = Intent(this@BaseActivity, LoginActivity::class.java)
//                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        JMessageClient.unRegisterEventReceiver(this);
        if (chat_dialog != null) {
            chat_dialog!!.dismiss()
        }
    }
}