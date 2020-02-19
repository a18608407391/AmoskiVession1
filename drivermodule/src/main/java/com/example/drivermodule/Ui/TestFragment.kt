package com.example.drivermodule.Ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TestViewModel
import com.example.drivermodule.databinding.FragmentTestBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.TEST_FR)
class TestFragment : BaseFragment<FragmentTestBinding, TestViewModel>() {
    override fun initContentView(): Int {
        return R.layout.fragment_test
    }

    override fun initVariableId(): Int {
        return BR.test_map
    }

}