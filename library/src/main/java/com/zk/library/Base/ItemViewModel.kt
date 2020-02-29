package com.zk.library.Base

import android.content.Intent
import android.support.annotation.NonNull
import com.zk.library.Bus.event.RxBusEven
import io.reactivex.disposables.Disposable
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


open class ItemViewModel<VM : BaseViewModel>() {
    lateinit var viewModel: VM
    var disposable: Disposable? = null

    open fun ItemViewModel(@NonNull viewModel: VM): ItemViewModel<VM> {
        this.viewModel = viewModel
        disposable = RxBus.default?.toObservable(RxBusEven::class.java)?.subscribe {
            doRxEven(it)
        }
        RxSubscriptions.add(disposable)
        return this
    }

    open fun doRxEven(it: RxBusEven?) {

    }

    open fun clearData() {

    }

    open fun initDatas(t: Int) {
    }



    fun destroy() {
        RxSubscriptions.remove(disposable)
    }


}