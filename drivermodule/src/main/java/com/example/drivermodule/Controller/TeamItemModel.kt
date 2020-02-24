package com.example.drivermodule.Controller

import android.content.Intent
import android.view.View
import com.amap.api.maps.model.Marker
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.elder.zcommonmodule.Entity.UserInfo
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel


class TeamItemModel : ItemViewModel<MapFrViewModel>() {

    var markerList =  HashMap<Int,Marker>()
    var TeamInfo: TeamPersonInfo? = null
    lateinit var user: UserInfo
    var teamer: Int = 0
    fun doCreate(data: Intent?) {

    }

    fun onComponentFinish() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun custionView(maker: Marker?, view: View?) {

    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun endTeam(b: Boolean) {

    }

    //组队逻辑处理






}