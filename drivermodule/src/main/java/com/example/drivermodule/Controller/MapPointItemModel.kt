package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.navi.view.RouteOverLay
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.RegeocodeResult
import com.chad.library.adapter.base.BaseQuickAdapter
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Prepare_Navigation
import com.elder.zcommonmodule.converLatPoint
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Adapter.AddPointAdapter
import com.example.drivermodule.Adapter.AddPointItemAdapter
import com.example.drivermodule.BR
import com.example.drivermodule.CalculateRouteListener
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.Entity.RouteDetailEntity
import com.example.drivermodule.Entity.RouteEntity
import com.example.drivermodule.Overlay.DrivingRouteOverlay
import com.example.drivermodule.Overlay.NaviDrivingRouteOverlay
import com.example.drivermodule.R
import com.example.drivermodule.Sliding.SlidingUpPanelLayout
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.Utils.MapUtils
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel
import com.zk.library.Bus.event.RxBusEven
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat


class MapPointItemModel : ItemViewModel<MapFrViewModel>(), BaseQuickAdapter.OnItemClickListener, CalculateRouteListener {

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    //地图选点逻辑处理
    var startMaker: Marker? = null
    var screenMaker: Marker? = null
    var dataEmpty = ObservableField<Boolean>(false)
    var finalyText = ObservableField<String>(getString(R.string.location_select))
    var choiceVisible = ObservableField<Boolean>(true)
    var pointList = ArrayList<PointEntity>()
    var finallyMarker: Marker? = null
    var panelState = ObservableField<SlidingUpPanelLayout.PanelState>(SlidingUpPanelLayout.PanelState.HIDDEN)
    lateinit var adapter: AddPointItemAdapter
    var SingleList = ArrayList<PointEntity>().apply {
        this.add(PointEntity("", LatLonPoint(0.0, 0.0)))
    }

    fun SearchResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    fun onComponentFinish() {
    }


    fun onInfoWindowClick(it: Marker?) {
        addPoint(it!!)
    }


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.DriverMapPointRegeocodeSearched -> {
                var regeocdeResult = it.value as RegeocodeResult
                var addressName = ""
                if (regeocdeResult.regeocodeAddress?.aois != null && regeocdeResult.regeocodeAddress?.aois?.size != 0) {
                    if (regeocdeResult.regeocodeAddress.aois[0].aoiName != null) {
                        addressName = regeocdeResult.regeocodeAddress?.district + regeocdeResult.regeocodeAddress.aois[0].aoiName
                    } else {
                        addressName = regeocdeResult.regeocodeAddress?.formatAddress!!
                    }
                } else {
                    addressName = regeocdeResult.regeocodeAddress?.formatAddress!!
                }

                Log.e("result", "addressName" + addressName)
                screenMaker?.title = addressName
                if (screenMaker != null) {
                    screenMaker?.showInfoWindow()
                }
            }
            RxBusEven.MapCameraChangeFinish -> {
                if (screenMaker != null) {
                    mapFr.mapUtils?.queryGeocoder(LatLonPoint(screenMaker?.position?.latitude!!, screenMaker?.position?.longitude!!))
                }
            }
        }
    }

    lateinit var mapFr: MapFragment
    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {
        mapFr = viewModel?.mapActivity
        adapter = AddPointItemAdapter(R.layout.district_item, SingleList)
        adapter.onItemClickListener = this
        adapter.setModel(this)
        return super.ItemViewModel(viewModel)
    }

    fun changeMap(curPosition: Location) {
        if (viewModel?.status.navigationStartPoint == null) {
            if (viewModel?.status.locationLat.size == 0) {
                viewModel?.status.locationLat.add(curPosition)
            }
            viewModel?.status.navigationStartPoint = curPosition
        }
        mapFr.mAmap.clear()
        startMaker = mapFr.mapUtils?.createMaker(Location(viewModel.status.navigationStartPoint!!.latitude, viewModel.status.navigationStartPoint!!.longitude))
        screenMaker = mapFr.mapUtils?.createScreenMarker()
        mapFr.mapUtils?.caculateRouteListener = this
        mapFr.mapUtils?.queryGeocoder(LatLonPoint(viewModel?.status.navigationStartPoint!!.latitude, viewModel?.status.navigationStartPoint!!.longitude))
    }

    fun onClick(view: View) {

    }

    fun addPoint(it: Marker) {
        if (finallyMarker == null) {
            //添加终点，并显示路线轨迹
            viewModel.status.navigationEndPoint = Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title)
            finalyText?.set(it.title)
            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finaly_point))
            finallyMarker = mapFr?.mAmap!!.addMarker(MarkerOptions().position(it.position).anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
            mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel?.status?.passPointDatas!!)
//            if (mapActivity.getDrverFragment().viewModel?.status?.startDriver?.get() == DriverCancle) {
//                mapActivity.getDrverFragment().viewModel?.isCanclePrepare = true
//                mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(Prepare_Navigation)
//            } else {
//                mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(Prepare_Navigation)
//            }
        } else {
            if (viewModel?.status?.passPointDatas?.size!! < 16) {
                viewModel?.status?.passPointDatas?.add(Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title))
                pointList?.add(PointEntity(it.title, converLatPoint(it.position)))
                var PointEntity = SingleList?.get(0)
                PointEntity?.address = getString(R.string.way_point) + pointList?.size + "个"
                SingleList?.set(0, PointEntity!!)
                adapter?.notifyDataSetChanged()
                mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel!!.status?.passPointDatas!!)
                if (viewModel?.status?.startDriver?.get() == DriverCancle) {
                    viewModel?.status?.startDriver?.set(DriverCancle)
                } else {
                    viewModel?.status?.startDriver?.set(Prepare_Navigation)
                }
            } else {
                Toast.makeText(context, getString(R.string.max_passPoint), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun CalculateCallBack(result: AMapCalcRouteResult) {
        //处理
//        var id = result?.routeid
//        var path = mapFr.mapUtils?.navi?.naviPaths!![id]


        Log.e("result", "result.errorCode" + result.errorCode)
        result?.routeid!!.forEachIndexed { index, it ->
            var path = mapFr.mapUtils?.navi?.naviPaths!![it]
            var entity = RouteEntity()
            if (index == 0) {
                entity.select.set(true)
            } else {
                entity.select.set(false)
            }
            entity.id.set(it)

            entity.title.set(path?.labels)
            if (path?.tollCost!! < 1) {
                entity.distance.set(DecimalFormat("0.0").format(path?.allLength!! / 1000) + "KM")
            } else {
                entity.distance.set(DecimalFormat("0.0").format(path?.allLength!! / 1000) + "KM" + " " + path?.tollCost + "￥")
            }
            entity.time.set(ConvertUtils.millis2FitTimeSpan(path?.allTime!! * 1000.toLong(), 3))
            items.add(entity)
        }
        drawRouteLine(items[0])
    }

    var routeDistance = 0
    var routeTime = 0
    private fun drawRouteLine(routeEntity: RouteEntity?) {
        var path = mapFr.mapUtils?.navi?.naviPaths!![routeEntity!!.id.get()]
        RoadDetailItems?.clear()
        var entity = RouteDetailEntity()
        entity.position = 0
        RoadDetailItems.add(entity)
        path?.steps?.forEachIndexed { index, it ->
            var item = RouteDetailEntity()
            item.position = index + 1
            item.iconType = it.iconType
            item.roadName = it.links.get(0).roadName
            if (it.length < 1000) {
                item.distance = it.length.toString() + "米"
            } else {
                item.distance = DecimalFormat("0.0").format(it.length / 1000) + "公里"
            }
            if (it.trafficLightCount > 0) {
                item.distance = item.distance + "红绿灯" + it.trafficLightCount + "个"
            }
            RoadDetailItems.add(item)
        }
        RoadDetailItems.sortBy {
            it.position
        }
        var drivingRouteOverlay = NaviDrivingRouteOverlay(context, mapFr.mAmap, path, path?.coordList!!.get(0), if (finallyMarker == null) path.endPoint else NaviLatLng(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), path!!.wayPoint)
        mapFr.mAmap.isMyLocationEnabled = false
        drivingRouteOverlay.setNodeIconVisibility(true)//设置节点marker是否显示
        drivingRouteOverlay.setThroughPointIconVisibility(true)
        drivingRouteOverlay.setIsColorfulline(true)//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap()
        drivingRouteOverlay.addToMap()
        routeDistance = path.allLength
        routeTime = path.allTime
        if (path.wayPoint.size == 0) {
            screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(LatLonPoint(viewModel.status.navigationStartPoint!!.latitude, viewModel.status.navigationStartPoint!!.longitude)))
            finallyMarker!!.remove()
            finallyMarker = null
            startMaker?.remove()
            startMaker = null
            startMaker = drivingRouteOverlay?.startMarker
            finallyMarker = drivingRouteOverlay?.endMarker
            drivingRouteOverlay.zoomSpanAll()
        } else {
            screenMaker = drivingRouteOverlay.addRemoveMarker(drivingRouteOverlay.convertToLatLng(path!!.wayPoint[path.wayPoint.size - 1]))
        }
        if (panelState.get() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            panelState.set(SlidingUpPanelLayout.PanelState.COLLAPSED)
        }
    }

    fun onComponentClick() {

    }

    var items = ObservableArrayList<RouteEntity>()

    var bindingCommand = BindingCommand(object : BindingConsumer<RouteEntity> {
        override fun call(t: RouteEntity) {
            if (!t.select.get()!!) {
                var list = ArrayList<RouteEntity>()
                items.forEach {
                    it.select.set(false)
                    list.add(it)
                }
                var index = list.indexOf(t)
                t.select.set(true)
                list.set(index, t)
                items.clear()
                items.addAll(list)
            }
        }
    })

    var RoadDetailItems = ObservableArrayList<RouteDetailEntity>()

    var RoadDetailItemsBinding = ItemBinding.of<RouteDetailEntity> { itemBinding, position, item ->
        if (item.position == 0) {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_first_layout)
        } else if (item.position == 999) {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_first_layout)
        } else {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_layout)
        }
    }

    var RoadDetailAdapter = BindingRecyclerViewAdapter<RouteDetailEntity>()

}