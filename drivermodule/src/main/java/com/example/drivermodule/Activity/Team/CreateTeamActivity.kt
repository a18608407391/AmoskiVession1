package com.example.drivermodule.Activity.Team

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.REQUEST_CREATE_JOIN
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.CreateTeamViewModel
import com.example.drivermodule.databinding.ActivityCreateTeamBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.TeamModule.TEAM_CREATE)
class CreateTeamActivity : BaseActivity<ActivityCreateTeamBinding, CreateTeamViewModel>() {
    override fun initVariableId(): Int {
        return BR.create_team
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_create_team
    }

    override fun initViewModel(): CreateTeamViewModel? {
        return ViewModelProviders.of(this)[CreateTeamViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }


    override fun doPressBack() {
        super.doPressBack()
        var intent = Intent()
        intent.putExtra("type", "cancle")
        setResult(REQUEST_CREATE_JOIN, intent)
        finish()
    }

}