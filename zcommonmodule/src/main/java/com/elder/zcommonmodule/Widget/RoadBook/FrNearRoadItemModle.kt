package com.elder.zcommonmodule.Widget.RoadBook

import android.databinding.ObservableArrayList
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.elder.zcommonmodule.BR
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_road_book_dialog.*
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat


class FrNearRoadItemModle : ItemViewModel<RoadBookHomeViewModel>, HttpInteface.getRoadBookList {
    override fun getRoadBookSuccess(it: String) {
        acRoadBookViewModel.roadHomeActivity.dialog_swipe.isRefreshing = false
        acRoadBookViewModel?.roadHomeActivity.dissmissProgress()
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        if (list.isNullOrEmpty()) {
            return
        }
        if (page == 1) {
            items.clear()
        }
        list.forEach {
            var distance = AMapUtils.calculateLineDistance(LatLng(acRoadBookViewModel.roadHomeActivity.location!!.latitude, acRoadBookViewModel.roadHomeActivity!!.location!!.longitude), LatLng(it.lat, it.lng))
            it.distance = distance
            if (it.distance < 1000) {
                it.distanceTv = DecimalFormat("0.0").format(it.distance) + "M"
            } else {
                it.distanceTv = DecimalFormat("0.0").format(it.distance / 1000) + "KM"
            }
            items.add(it)
        }
        items.sortBy {
            it.distance
        }
    }

    override fun getRoadBookError(ex: Throwable) {
        viewModel?.roadHomeActivity.dissmissProgress()
        acRoadBookViewModel.roadHomeActivity.dialog_swipe.isRefreshing = false
    }

    fun ItemClickCommand(data: HotData) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data).navigation(acRoadBookViewModel.roadHomeActivity.activity, REQUEST_LOAD_ROADBOOK)

//        var intent = Intent()
//        intent.putExtra("hotdata", data)
//        acRoadBookViewModel.roadHomeActivity.setResult(REQUEST_LOAD_ROADBOOK, intent)
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
        map["lat"] = acRoadBookViewModel.roadHomeActivity.location!!.latitude.toString()
        map["lng"] = acRoadBookViewModel.roadHomeActivity.location!!.longitude.toString()
        map["orderByType"] = "2"
        HttpRequest.instance.getRoadBookList(map)
    }


    var adapter = FrStrageAdapter()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.fr_hot_data, R.layout.fr_near_recy_item_layout).bindExtra(BR.near_item_model, this@FrNearRoadItemModle)
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