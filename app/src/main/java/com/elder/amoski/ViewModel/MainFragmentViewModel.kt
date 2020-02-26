package com.elder.amoski.ViewModel

import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.widget.RadioGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Fragment.SocialFragment
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.Fragment.HomeFragment
import com.elder.amoski.R
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.CALL_BACK_STATUS
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.zk.library.Bus.event.RxBusEven
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.Ui.MapFragment
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.util.ArrayList


class MainFragmentViewModel : BaseViewModel, RadioGroup.OnCheckedChangeListener {

    var lastCheckediD = R.id.same_city
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.same_city -> {
                var fr = mFragments[0] as LogRecodeFragment
                if (fr.curOffset > -ConvertUtils.dp2px(122F)) {
                    Utils.setStatusTextColor(false, homeActivity.activity as HomeActivity)
                } else {
                    Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                }
                changerFragment(0)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.same_city
            }
            R.id.main_left -> {
                Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                changerFragment(1)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_left
            }
            R.id.driver_middle -> {
                Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                changerFragment(2)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.driver_middle
                checkModel = 1
            }
            R.id.dynamics -> {
                Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                changerFragment(3)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.dynamics
            }
            R.id.main_right -> {
                Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                changerFragment(4)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_right
                checkModel = 0
            }
        }
    }


    var returnCheckId = 0
    var checkModel = 0
    var bottomVisible = ObservableField<Boolean>(true)
    lateinit var homeActivity: HomeFragment
    var mFragments = ArrayList<Fragment>()
    var type = 2
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var bottomBg = ObservableField<Drawable>(context.getDrawable(R.drawable.home_bottom_bg))
    var logSelected = ObservableField<Boolean>()
    var driverSelected = ObservableField<Drawable>()
    var privateSelected = ObservableField<Boolean>()
    var curPosition = 0


    var myself: Fragment? = null
    var party: Fragment? = null
    var social: SocialFragment? = null
    var logmodule: LogRecodeFragment? = null
    var mapFr: MapFragment? = null

    var tans: FragmentTransaction? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(homeActivity: HomeFragment) {
        this.homeActivity = homeActivity
        returnCheckId = R.id.same_city
        changerFragment(0)
        RxSubscriptions.add(RxBus.default?.toObservable(RxBusEven::class.java)?.subscribe {
            when (it.type) {
                RxBusEven.DriverReturnRequest -> {
                    homeActivity.main_bottom_bg.check(lastCheckediD)
                    CoroutineScope(uiContext).launch {
                        bottomVisible.set(true)
                    }
                }
                RxBusEven.PartyWebViewReturn -> {
                    bottomVisible.set(it.value as Boolean)
                    if (it.secondValue as Int == 1) {
                        homeActivity.main_bottom_bg.check(lastCheckediD)
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun changerFragment(position: Int) {
        tans = homeActivity.childFragmentManager.beginTransaction()
        if (position == 0) {
            if (logmodule == null) {
                logmodule = ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogRecodeFR).navigation() as LogRecodeFragment
                mFragments.add(logmodule!!)
                tans?.add(R.id.rootlayout, logmodule!!)
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            logSelected.set(true)
            privateSelected.set(false)
        } else if (position == 1) {
            if (party == null) {
                party = ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_MAIN).navigation() as Fragment
                mFragments.add(party!!)
                tans!!.add(R.id.rootlayout, party!!)
            }

        } else if (position == 2) {

            if (mapFr == null) {
                mapFr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_FR).navigation() as MapFragment?
                mFragments.add(mapFr!!)
                tans!!.add(R.id.rootlayout, mapFr!!)
            } else {
//                mapFr!!.setDark()
            }
            bottomVisible.set(false)

        } else if (position == 3) {
            if (social == null) {
                social = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_MAIN).navigation() as SocialFragment
                mFragments.add(social!!)
                tans?.add(R.id.rootlayout, social!!)
            }

//            homeActivity.main_layout.setBackgroundColor(getColor(R.color.blackTextColor))
//            logSelected.set(false)
//            if (type == 2) {
//                driverSelected.set(context.getDrawable(R.drawable.start_driver))
//            } else {
//                driverSelected.set(context.getDrawable(R.drawable.driving_icon_big))
//            }
//            bottomBg.set(context.getDrawable(R.drawable.big_circle_bottom_bg))
//            privateSelected.set(false)

        } else if (position == 4) {
            if (myself == null) {
                myself = ARouter.getInstance().build(RouterUtils.FragmentPath.MYSELFPAGE).navigation() as Fragment
                mFragments.add(myself!!)
                tans?.add(R.id.rootlayout, myself!!)
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            var fr = myself as UserInfoFragment
            fr.getUserInfo(true)
            logSelected.set(false)
            privateSelected.set(true)
        }
        curPosition = position
        mFragments!!.forEach {
            tans!!.hide(it)
        }
        if (position == 1) {
            tans!!.show(party!!)
        } else if (position == 2) {
            tans!!.show(mapFr!!)
        } else if (position == 3) {
            tans!!.show(social!!)
        } else if (position == 4) {
            tans!!.show(myself!!)
        } else {
            tans!!.show(mFragments!![position])
        }
        tans!!.commitAllowingStateLoss()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.main_left -> {
                if (curPosition == 0) {
                    return
                }
                changerFragment(0)
            }
            R.id.main_right -> {
                if (curPosition == 1) {
                    return
                }
                changerFragment(1)
            }
        }
    }

    constructor() {
        var m = RxBus.default?.toObservable(ActivityResultEven::class.java)?.subscribe {
            when (it.name) {
                CALL_BACK_STATUS -> {
                    type = it.data as Int
                    if (type == 2) {
                        driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                    } else {
                        driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
                    }
                }
            }
        }

        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ShareFinish") {
                homeActivity.main_bottom_bg.check(R.id.same_city)
                var log = mFragments[0] as LogRecodeFragment
                log.loadDatas(log.viewModel?.location!!)
                type = DriverCancle
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                AppManager.get()?.finishOtherActivity(homeActivity.javaClass)
            } else if (it == "ToUser") {
                (homeActivity.activity as HomeActivity).runOnUiThread {
                    homeActivity.main_bottom_bg.check(R.id.main_right)
                }
            }
        }
        RxSubscriptions.add(m)
        RxSubscriptions.add(s)
    }
}