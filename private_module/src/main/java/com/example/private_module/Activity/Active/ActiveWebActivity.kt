package com.example.private_module.Activity.Active

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.private_module.BR
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.Active.ActiveWebViewModel
import com.example.private_module.databinding.ActiveWebActivityBinding
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC)
class ActiveWebActivity : BaseActivity<ActiveWebActivityBinding, ActiveWebViewModel>(){

    @Autowired(name = RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE)
    @JvmField
    var type: Int = 0

    @Autowired(name = RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_ID)
    @JvmField
    var id: String? = null

    override fun initVariableId(): Int {
        return BR.active_web_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.active_web_activity
    }

    override fun initViewModel(): ActiveWebViewModel? {
        return ViewModelProviders.of(this)[ActiveWebViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel?.startWx!!) {
            mViewModel?.webUrl!!.set("http://yomoy.com.cn/AmoskiWebActivity/personalcenter/roadbookActivitype/order/orderPayment.html?isWeiXin=true")
            mViewModel?.startWx = false
            showProgressDialog(getString(R.string.http_loading))
        }
    }
}