package com.example.drivermodule.Controller

import android.view.View
import com.amap.api.maps.model.Marker
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.Entity.RoadBook.RoadDetailEntity
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.elder.zcommonmodule.Component.ItemViewModel


class RoadBookItemModel : ItemViewModel<MapFrViewModel>() {
    var netWorkData: ArrayList<RoadDetailEntity>? = null
    var data: HotData? = null
    fun onComponentFinish() {
    }

    fun customView(maker: Marker, view: View?) {

    }

    fun onInfoWindowClick(it: Marker?) {

    }
    fun doLoadDatas(data: HotData) {

    }
    fun backToDriver() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //路书逻辑处理


}