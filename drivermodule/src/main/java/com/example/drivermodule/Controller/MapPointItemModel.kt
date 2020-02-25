package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.view.View
import android.widget.Toast
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.services.core.LatLonPoint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Prepare_Navigation
import com.elder.zcommonmodule.converLatPoint
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Adapter.AddPointAdapter
import com.example.drivermodule.Adapter.AddPointItemAdapter
import com.example.drivermodule.CalculateRouteListener
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.Entity.RouteEntity
import com.example.drivermodule.Overlay.DrivingRouteOverlay
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat


class MapPointItemModel : ItemViewModel<MapFrViewModel>(), BaseQuickAdapter.OnItemClickListener, CalculateRouteListener {
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    //地图选点逻辑处理
    var startMaker: Marker? = null
    var screenMaker: Marker? = null
    var dataEmpty = ObservableField<Boolean>(false)
    var finalyText = ObservableField<String>(getString(R.string.location_select))
    var choiceVisible = ObservableField<Boolean>(false)
    var pointList = ArrayList<PointEntity>()
    var finallyMarker: Marker? = null
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


    override fun CalculateCallBack(result: AMapCalcRouteResult?) {
        //处理
//        var id = result?.routeid
//        var path = mapFr.mapUtils?.navi?.naviPaths!![id]
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

    private fun drawRouteLine(routeEntity: RouteEntity?) {
//        var path = mapFr.mapUtils?.navi?.naviPaths!![routeEntity!!.id.get()]
//        var drivingRouteOverlay = DrivingRouteOverlay(context, mapFr.mAmap, p0?.paths!![index], p0?.startPos, if (mapPointController.finallyMarker == null) p0?.targetPos else LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), p0?.driveQuery!!.passedByPoints)
//        mapActivity.mAmap.isMyLocationEnabled = false
//        drivingRouteOverlay.setNodeIconVisibility(true)//设置节点marker是否显示
//        drivingRouteOverlay.setThroughPointIconVisibility(true)
//        drivingRouteOverlay.setIsColorfulline(true)//是否用颜色展示交通拥堵情况，默认true
//        drivingRouteOverlay.removeFromMap()
//        drivingRouteOverlay.addToMap()
//        routeDistance = p0?.paths!![index].distance
//        routeTime = p0?.paths!![index].duration
//        if (!p0!!.driveQuery.hasPassPoint()) {
//            mapPointController.screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(LatLonPoint(driverModel.status.navigationStartPoint!!.latitude, driverModel.status.navigationStartPoint!!.longitude)))
//            mapPointController.finallyMarker = drivingRouteOverlay?.endMarker
//            drivingRouteOverlay.zoomSpanAll()
//        } else {
//            mapPointController.screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(p0?.driveQuery!!.passedByPoints[p0?.driveQuery!!.passedByPoints.size - 1]))
//        }
    }

    var items = ObservableArrayList<RouteEntity>()
}