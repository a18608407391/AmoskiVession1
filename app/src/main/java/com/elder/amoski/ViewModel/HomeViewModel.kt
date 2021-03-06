package com.elder.amoski.ViewModel

import com.elder.amoski.Activity.HomeActivity
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven


class HomeViewModel : BaseViewModel() {
    //    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
//        when (checkedId) {
//            R.id.same_city -> {
//                var fr = mFragments[0] as LogRecodeFragment
//                if (fr.curOffset > -ConvertUtils.dp2px(122F)) {
//                    Utils.setStatusTextColor(false, homeActivity)
//                } else {
//                    Utils.setStatusTextColor(true, homeActivity)
//                }
//
////                StatusbarUtils.setStatusBarMode(homeActivity, false, 0x000000)
//                changerFragment(0)
//                lastChecked = checkedId
//            }
//            R.id.main_left -> {
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(1)
//                lastChecked = checkedId
//            }
//            R.id.driver_middle -> {
//                if (!NetworkUtil.isNetworkAvailable(homeActivity)) {
//                    Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (type != DriverCancle) {
//                    val intent = Intent()
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
//                    intent.setClass(homeActivity, MapActivity::class.java)
//                    intent.putExtra(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
//                    homeActivity.startActivity(intent)
//                } else {
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal").navigation()
//                }
//                homeActivity.main_bottom_bg.check(lastChecked)
//            }
//            R.id.dynamics -> {
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(2)
//                lastChecked = checkedId
//            }
//            R.id.main_right -> {
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(3)
//                lastChecked = checkedId
//            }
//        }
//    }
//
//
//    var lastChecked = 0
//    var bottomVisible = ObservableField<Boolean>(true)
//
//    lateinit var homeActivity: HomeActivity
//    var mFragments = ArrayList<Fragment>()
//    var bottomSrc = ObservableField<Drawable>()
//    var type = 2
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    var bottomBg = ObservableField<Drawable>(context.getDrawable(R.drawable.home_bottom_bg))
//
//    var logSelected = ObservableField<Boolean>()
//
//    var driverSelected = ObservableField<Drawable>()
//
//    var privateSelected = ObservableField<Boolean>()
//
//    var cityelected = ObservableField<Boolean>()
//
//
//    var activeSelected = ObservableField<Boolean>()
//    var curPosition = 0
//
//
//    var myself: Fragment? = null
//    var party: Fragment? = null
//    var social: SocialFragment? = null
//    var logmodule: LogRecodeFragment? = null
//
//    var tans: FragmentTransaction? = null
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(homeActivity: HomeActivity) {
//        this.homeActivity = homeActivity
//        lastChecked = R.id.same_city
//        changerFragment(0)
//        Utils.setStatusTextColor(false, homeActivity)
//        RxSubscriptions.add(RxBus.default?.toObservable(BooleanEven::class.java)?.subscribe {
//            bottomVisible.set(it.flag)
//            if (it.type == 1) {
//                homeActivity.main_bottom_bg.check(R.id.main_right)
//            }
//        })
    }
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun changerFragment(position: Int) {
//        tans = homeActivity.supportFragmentManager.beginTransaction()
//        if (position == 0) {
//            if (logmodule == null) {
//                logmodule = ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogRecodeFR).navigation() as LogRecodeFragment
//                mFragments.add(logmodule!!)
//                tans?.add(R.id.rootlayout, logmodule!!)
//            }
//            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
//            if (type == 2) {
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//            } else {
//                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//            }
//            logSelected.set(true)
//            privateSelected.set(false)
//
//        } else if (position == 1) {
//            if (party == null) {
//                party = ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_MAIN).navigation() as Fragment
//                mFragments.add(party!!)
//                tans!!.add(R.id.rootlayout, party!!)
//            }
//        } else if (position == 2) {
//            if (social == null) {
//                social = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_MAIN).navigation() as SocialFragment
//                mFragments.add(social!!)
//                tans?.add(R.id.rootlayout, social!!)
//            }
//
////            homeActivity.main_layout.setBackgroundColor(getColor(R.color.blackTextColor))
////            logSelected.set(false)
////            if (type == 2) {
////                driverSelected.set(context.getDrawable(R.drawable.start_driver))
////            } else {
////                driverSelected.set(context.getDrawable(R.drawable.driving_icon_big))
////            }
////            bottomBg.set(context.getDrawable(R.drawable.big_circle_bottom_bg))
////            privateSelected.set(false)
//
//        } else if (position == 3) {
//            if (myself == null) {
//                myself = ARouter.getInstance().build(RouterUtils.FragmentPath.MYSELFPAGE).navigation() as Fragment
//                mFragments.add(myself!!)
//                tans?.add(R.id.rootlayout, myself!!)
//            }
//            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
//            if (type == 2) {
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//            } else {
//                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//            }
//            var fr = myself as UserInfoFragment
//            fr.getUserInfo(true)
//            logSelected.set(false)
//            privateSelected.set(true)
//        }
//        curPosition = position
//        mFragments!!.forEach {
//            tans!!.hide(it)
//        }
//        if (position == 1) {
//            tans!!.show(party!!)
//        } else if (position == 2) {
//            tans!!.show(social!!)
//        } else if (position == 3) {
//            tans!!.show(myself!!)
//        } else {
//            tans!!.show(mFragments!![position])
//        }
//        tans!!.commitAllowingStateLoss()
//    }
//
//    fun onClick(view: View) {
//        when (view.id) {
//            R.id.main_img_src -> {
//                if (!NetworkUtil.isNetworkAvailable(homeActivity)) {
//                    Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (type != DriverCancle) {
//                    val intent = Intent()
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
//                    intent.setClass(homeActivity, MapActivity::class.java)
//                    intent.putExtra(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
//                    homeActivity.startActivity(intent)
//                } else {
////                    var flag = PreferenceUtils.getBoolean(context, SERVICE_AUTO_BOOT_COMPLETED)
////                    if (!flag || !OSUtil.checkIgnoreBattery(homeActivity)) {
////                        var dialog = DialogUtils.createNomalDialog(homeActivity, getString(R.string.checked_not_white), getString(R.string.setting_permisstion), getString(R.string.continue_driving))
////                        dialog.setOnBtnClickL(OnBtnClickL {
////                            dialog.dismiss()
////                            ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0).navigation()
////                        }, OnBtnClickL {
////                            dialog.dismiss()
////                            context.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
////                            ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal").navigation()
////                        })
////                        dialog.show()
////                    } else {
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal").navigation()
////                    }
//                }
//            }
//
//            R.id.main_left -> {
//                if (curPosition == 0) {
//                    return
//                }
//                changerFragment(0)
//            }
//            R.id.main_right -> {
//                if (curPosition == 1) {
//                    return
//                }
//                changerFragment(1)
//            }
//        }
//    }
//
//    constructor() {
//        var m = RxBus.default?.toObservable(ActivityResultEven::class.java)?.subscribe {
//            when (it.name) {
//                CALL_BACK_STATUS -> {
//                    type = it.data as Int
//                    if (type == 2) {
//                        driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//                    } else {
//                        driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//                    }
//                }
//            }
//        }
//
//        var n = RxBus.default?.toObservableSticky(DriverDataStatus::class.java)?.subscribe {
//
//        }
//        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
//            if (it == "ShareFinish") {
//                homeActivity.main_bottom_bg.check(R.id.same_city)
//                var log = mFragments[0] as LogRecodeFragment
//                log.loadDatas(log.viewModel?.location!!)
//                type = DriverCancle
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//                AppManager.get()?.finishOtherActivity(homeActivity.javaClass)
//            } else if (it == "ToUser") {
//                homeActivity.runOnUiThread {
//                    homeActivity.main_bottom_bg.check(R.id.main_right)
//                }
//            }
//        }
//        RxSubscriptions.add(m)
//        RxSubscriptions.add(s)
//        RxSubscriptions.add(n)
//    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.BrowserSendTeamCode -> {
                var teamCode = it.value as String
                //TODO
            }
        }
    }
}