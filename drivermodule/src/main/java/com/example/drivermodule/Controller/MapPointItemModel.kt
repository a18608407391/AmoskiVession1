package com.example.drivermodule.Controller

import android.content.Intent
import com.amap.api.maps.model.Marker
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel


class MapPointItemModel : ItemViewModel<MapFrViewModel>() {

    fun SearchResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    fun onComponentFinish() {
    }


    fun onInfoWindowClick(it: Marker?) {

    }
    //地图选点逻辑处理


}