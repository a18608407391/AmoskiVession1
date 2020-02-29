package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableField
import android.view.View
import com.amap.api.maps.model.Marker
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.elder.zcommonmodule.Entity.UserInfo
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketConnectSuccess
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketDisConnect


class TeamItemModel : ItemViewModel<MapFrViewModel>() {
    //组队逻辑处理
    var markerList = HashMap<Int, Marker>()
    var TeamInfo: TeamPersonInfo? = null
    var teamer: Int = 0
    var teamCode = ObservableField<String>()
    var teamId: String? = null
    var teamName: String? = null
    var markerListNumber = ArrayList<String>()
    var titleName = ObservableField<String>()
    var date = ObservableField<String>()
    var districtTv = ObservableField<String>()
    var visibleScroller = ObservableField<Boolean>(true)
    var teamNavigation = ObservableField<Boolean>(false)
    var create: CreateTeamInfoDto? = null

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            TeamSocketConnectSuccess -> {
                //组队Socket链接成功
            }
            TeamSocketDisConnect -> {
                //组队Socket已断开
            }
        }
    }

    fun doCreate(data: Intent?) {

    }

    fun onClick(view: View) {

    }

    fun onComponentFinish() {

    }

    fun custionView(maker: Marker?, view: View?) {

    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun endTeam(b: Boolean) {

    }

    fun backToDriver() {
    }

    fun backToRoad() {
    }


}