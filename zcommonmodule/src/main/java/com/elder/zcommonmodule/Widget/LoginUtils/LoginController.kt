package com.elder.zcommonmodule.Widget.LoginUtils

import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import java.lang.ref.WeakReference


class LoginController {

    private val TAG = "CityPicker"

    private lateinit var mContext: WeakReference<FragmentActivity>
    private lateinit var mFragment: WeakReference<Fragment>
    private var mFragmentManager: WeakReference<FragmentManager>? = null
    private var enableAnim: Boolean = true
    private var mAnimStyle: Int = 0

    constructor(fragment: Fragment) {
        LoginController(fragment.activity, fragment)
        mFragmentManager = WeakReference(fragment.childFragmentManager)
    }

    constructor(activity: FragmentActivity) {
        LoginController(activity, null)
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    constructor(activity: FragmentActivity?, fragment: Fragment?) {
        mContext = WeakReference<FragmentActivity>(activity)
        mFragment = WeakReference<Fragment>(fragment)
    }

    fun from(fragment: Fragment): LoginController {
        return LoginController(fragment)
    }

    fun from(activity: FragmentActivity): LoginController {
        return LoginController(activity)
    }

    /**
     * 设置动画效果
     * @param animStyle
     * @return
     */
    fun setAnimationStyle(@StyleRes animStyle: Int): LoginController {
        this.mAnimStyle = animStyle
        return this@LoginController
    }

    /**
     * 启用动画效果，默认为false
     * @param enable
     * @return
     */
    fun enableAnimation(enable: Boolean): LoginController {
        this.enableAnim = enable
        return this
    }

    /**
     * 设置选择结果的监听器
     * @param listener
     * @return
     */

    fun show(dialog: BaseDialogFragment): BaseDialogFragment {
        var ft = mFragmentManager!!.get()?.beginTransaction()
        val prev = mFragmentManager!!.get()?.findFragmentByTag(TAG)
        if (prev != null) {
            ft?.remove(prev)!!.commit()
            ft = mFragmentManager!!.get()!!.beginTransaction()
        }
        ft?.addToBackStack(null)
        dialog.setAnimationStyle(mAnimStyle)
        dialog.show(ft, TAG)
        return dialog
    }

    /**
     * 定位完成
     * @param location
     * @param state
     */

}