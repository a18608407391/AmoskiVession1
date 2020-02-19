package com.elder.amoski.Fragment

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.MainFragmentViewModel
import com.elder.amoski.databinding.FragmentMainBinding
import com.zk.library.Base.BaseFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_main.*


class HomeFragment : BaseFragment<FragmentMainBinding, MainFragmentViewModel>() {


    companion object {
        fun newInstance(): HomeFragment {
            var args = Bundle()
            var fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun initContentView(): Int {
        return R.layout.fragment_main
    }

    override fun initVariableId(): Int {
        return BR.main_fr_viewmodel
    }

    override fun initData() {
        super.initData()
        main_bottom_bg.setOnCheckedChangeListener(viewModel)
        viewModel?.inject(this)
    }


}