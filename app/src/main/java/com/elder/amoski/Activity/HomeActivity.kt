package com.elder.amoski.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.HomeViewModel
import com.elder.amoski.databinding.ActivityHomeBinding
import com.elder.zcommonmodule.Entity.UserInfo
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Entity.HotData
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import android.content.ClipData
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.elder.amoski.Fragment.HomeFragment
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Even.RequestErrorEven
import com.elder.zcommonmodule.Utils.Utils
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.Transaction.anim.DefaultHorizontalAnimator
import com.zk.library.Base.Transaction.anim.FragmentAnimator
import org.cs.tec.library.Bus.RxSubscriptions
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


@Route(path = RouterUtils.ActivityPath.HOME)
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    override fun initVariableId(): Int {
        return BR.HomeViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
//        Utils.setStatusBar(this, false, false)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        val fragmentManagerImpl = fragmentManager as FragmentManagerImpl
        return R.layout.activity_home
    }


    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY)
    @JvmField
    var resume: String? = null

    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE)
    @JvmField
    var teamCode: String? = null

    override fun initViewModel(): HomeViewModel? {
        return ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        BaseApplication.getInstance().curActivity = 1
        var key = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip = key.primaryClip
        if (clip != null && clip.itemCount > 0) {
            var tv = clip.getItemAt(0).text.toString()
            if (!tv.isNullOrEmpty() && tv.contains("Amoski:HDID=")) {
                var number = tv.split("=")[1]
                var dialog = DialogUtils.createNomalDialog(this@HomeActivity, getString(R.string.active_notify), getString(R.string.cancle), getString(R.string.confirm))
                dialog.show()
                dialog.setOnBtnClickL(OnBtnClickL {
                    key.clearPrimaryClip()
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }, OnBtnClickL {
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                    clip = ClipData.newPlainText("label", "")
                    key.primaryClip = clip
                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 4).withString(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_ID, number).navigation()
                })
            }
        }
    }


    var homeFr: HomeFragment? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {
        super.initData()
        homeFr = HomeFragment.newInstance()
        loadRootFragment(R.id.home_main_layout, homeFr!!)
        var pos = ServiceEven()
        pos.type = "HomeStart"
        RxBus.default?.post(pos)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
        mViewModel?.inject(this)

        RxSubscriptions.add(RxBus.default?.toObservable(RequestErrorEven::class.java)?.subscribe {
            if (it.errorCode == 10009) {
                RxBus.default?.post("ExiLogin")
                PreferenceUtils.putBoolean(context, RE_LOGIN, true)
                var pos = ServiceEven()
                pos.type = "HomeStop"
                RxBus.default?.post(pos)
                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(this, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (homeFr?.viewModel?.curPosition == 2) {
            homeFr?.viewModel?.mapFr?.onActivityResult(requestCode, resultCode, data)

        } else {
            when (requestCode) {
                GET_USERINFO -> {
                    if (data != null) {
                        var info = data?.extras!!["userInfo"] as UserInfo
                        var fr = homeFr?.viewModel?.myself as UserInfoFragment
                        fr.callback(info)
//                    var even = ActivityResultEven(requestCode, )
//                    RxBus.default?.post(even)
                    }
                }
                999 -> {
                    if (resultCode == 0) {
                        Toast.makeText(context, "后台活动开启未启动", Toast.LENGTH_SHORT).show()
                    } else if (resultCode == Activity.RESULT_OK) {
                        PreferenceUtils.putBoolean(context, "OPEN_GOD_MODEL", true)
                    }
                }
                REQUEST_LOAD_ROADBOOK -> {
                    if (data != null) {
                        var date = data.getSerializableExtra("hotdata") as HotData
                        RxBus.default?.post(date)
                        ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad").withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, date).navigation()
                    }
                }
                SOCIAL_DETAIL_RETURN -> {
                    if (data != null) {
                        homeFr?.viewModel?.social!!.initResult(data)
                    }
                }
                PRIVATE_DATA_RETURN -> {
                    var fr = homeFr?.viewModel?.myself as UserInfoFragment
                    fr.getUserInfo(false)
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    var time: Long = 0


    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }


    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

}