package com.zk.library.Base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxDialogFragment
import com.trello.rxlifecycle2.components.support.RxFragment
import com.zk.library.Base.Transaction.ISupportFragment
import java.lang.reflect.ParameterizedType


abstract class BaseFragmentDialog<V : ViewDataBinding, VM : BaseViewModel> : RxDialogFragment(), IBaseFragment {
    override fun initParam() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViewObservable() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var binding: V? = null
    public var viewModel: VM? = null
    protected var viewModelId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var k: Int = initContentView()
        binding = DataBindingUtil.inflate(inflater, k, container, false)
        viewModelId = initVariableId()
        viewModel = initViewModel()
        if (viewModel == null) {
            val modelClass: Class<*>
            val type = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<*>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java
            }
            viewModel = createViewModel(this, modelClass as Class<VM>)
        }
        binding!!.setVariable(viewModelId, viewModel)
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel!!)
        //注入RxLifecycle生命周期
        viewModel!!.injectLifecycleProvider(this)
//        initStatusLayout()


        return binding!!.root

    }

    abstract fun initContentView(): Int
    abstract fun initVariableId(): Int
    fun initViewModel(): VM? {
        return null
    }

    fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProviders.of(fragment).get(cls)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的ViewModel与View的契约事件回调逻辑
        //页面数据初始化方法
        initData()
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
        //注册RxBus
        viewModel!!.registerRxBus()
        initMap(savedInstanceState)
    }

    open fun initMap(savedInstanceState: Bundle?) {

    }
}