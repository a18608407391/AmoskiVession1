package com.zk.library.Base

import android.content.Intent
import android.support.annotation.NonNull


open class ItemViewModel<VM : BaseViewModel>() {
    lateinit var viewModel: VM

   open fun ItemViewModel(@NonNull viewModel: VM): ItemViewModel<VM> {
        this.viewModel = viewModel
       return this
    }

    open fun clearData() {

    }

  open  fun initDatas(t:Int) {
    }

    fun doLoadDatas(data: Intent) {

    }
}