package com.elder.zcommonmodule.Widget.RoadBook

import android.databinding.ObservableArrayList
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.BR
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.big_pic_layout.*
import kotlinx.android.synthetic.main.fragment_road_book_dialog.*
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class FrHotRoadItemModle :ItemViewModel<RoadBookHomeViewModel>, HttpInteface.getRoadBookList {
    override fun getRoadBookSuccess(it: String) {
        acRoadBookViewModel?.roadHomeActivity.dissmissProgress()
        acRoadBookViewModel.roadHomeActivity.dialog_swipe.isRefreshing = false
        if (it.isNullOrEmpty()) {
            return
        }
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        if (list.isNullOrEmpty()) {
            return
        }
        if (page == 1) {
            items.clear()
        }
        list.forEach {
            items.add(it)
        }
        if (acRoadBookViewModel.roadHomeActivity.type == 1 && category) {
            category = false
            acRoadBookViewModel.roadHomeActivity.dialog_viewpager.currentItem = 1
        }
    }


    var category = true
    override fun getRoadBookError(ex: Throwable) {
        acRoadBookViewModel
        acRoadBookViewModel.roadHomeActivity.dialog_swipe.isRefreshing = false
    }


    fun ItemClickCommand(date: HotData) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, date).navigation(acRoadBookViewModel.roadHomeActivity.activity, REQUEST_LOAD_ROADBOOK)

//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY,date).navigation(acRoadBookViewModel.roadHomeActivity,REQUEST_LOAD_ROADBOOK)

//        Log.e("result","传值"+Gson().toJson(date))
//        var intent = Intent()
//        intent.putExtra("hotdata", date)
//        acRoadBookViewModel.roadHomeActivity.setResult(REQUEST_LOAD_ROADBOOK,intent)
//        acRoadBookViewModel.roadHomeActivity.finish()
    }

    var acRoadBookViewModel: RoadBookHomeViewModel

    constructor(acRoadBookViewModel: RoadBookHomeViewModel, i: Int) {
        this.acRoadBookViewModel = acRoadBookViewModel
    }

    var page = 1

    override fun initDatas(i: Int) {
        super.initDatas(i)
        acRoadBookViewModel?.roadHomeActivity.showProgress(getString(R.string.http_loading_roadbook))
        HttpRequest.instance.RoadBookGuideList = this
        var map = HashMap<String, String>()
        map["limit"] = "10"
        map["page"] = page.toString()
        map["orderByType"] = "1"
        HttpRequest.instance.getRoadBookList(map)
    }

    var adapter = FrStrageAdapter()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.fr_hot_data, R.layout.fr_hot_recy_item_layout).bindExtra(BR.hot_item_model, this@FrHotRoadItemModle)
    }

    var listDatas = ObservableArrayList<HotBannerData>()
    var curLoad = 0
    var loadMore = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t > curLoad) {
                page++
                initDatas(page)
                curLoad = t
            }
        }
    })
}