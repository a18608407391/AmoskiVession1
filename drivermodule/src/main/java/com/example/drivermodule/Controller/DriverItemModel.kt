package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DataBases.insertDriverStatus
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Inteface.Locationlistener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Component.DriverItemController
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.Ui.TeamFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.ItemViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class DriverItemModel : ItemViewModel<MapFrViewModel>(), HttpInteface.startDriverResult, Locationlistener {
    override fun onLocation(location: AMapLocation) {
        location(location)
    }

    var timer: Observable<Long>? = null
    var timerDispose: Disposable? = null


    override fun startDriverSuccess(it: String) {
        viewModel.mapActivity.dismissProgressDialog()
        viewModel?.status.driverNetRecord = Gson().fromJson(it, StartRidingRequest::class.java)
        viewModel?.status.startDriver.set(Drivering)
        driverStatus.set(Drivering)
        timer = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        viewModel.status.StartTime = System.currentTimeMillis()
        viewModel?.component!!.Drivering.set(true)
        insertDriverStatus(viewModel?.status)
        timerDispose = timer?.subscribe {
            viewModel?.status.second++
            driverTime.set(ConvertUtils.formatTimeS(viewModel?.status.second))
            UpdateDriverStatus(viewModel?.status)
        }
        if (driverType == 0) {

        } else {
            //未骑行导航  从骑行界面搜索
            var wayPoint = ArrayList<LatLng>()
            if (!viewModel?.status?.passPointDatas.isNullOrEmpty()) {
                viewModel?.status?.passPointDatas.forEach {
                    wayPoint.add(LatLng(it.latitude, it.longitude))
                }
            }
            viewModel?.startNavi(viewModel?.status.navigationStartPoint!!, viewModel?.status.navigationEndPoint!!, wayPoint, driverType)
        }
    }


    override fun startDriverError(error: Throwable) {
        viewModel.mapActivity.dismissProgressDialog()
    }


    //骑行界面逻辑处理
    var shareUpLoad = UpDataDriverEntitiy()

    var behavior = ObservableField<Int>(BottomSheetBehavior.STATE_HIDDEN)

    var chart = ObservableField<DriverDataStatus>()
    //距离文本
    var driverDistance = ObservableField<String>("0M")
    //时间文本
    var driverTime = ObservableField<String>("00:")
    //开始骑行请求结果
    var bottomLayoutVisible = ObservableField<Boolean>(true)
    //骑行用户昵称文本
    var userName = ObservableField<String>()
    //结束时间文本
    var finishTime = ObservableField<String>()
    //骑行头像
    var deivceInfo = UIdeviceInfo(ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""))

    var driverAvatar = ObservableField<String>()
    var share = ShareEntity()

    var driverStatus = ObservableField(DriverCancle)

    var locationDispose: Disposable? = null

    lateinit var mapFr: MapFragment

    var driverController = DriverItemController(this)


    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {
        mapFr = viewModel?.mapActivity
        mapFr.showProgressDialog(getString(R.string.location_loading))
        RxSubscriptions.add(locationDispose)
        viewModel?.listeners = this

        driverStatus.set(viewModel?.status.startDriver.get())
        initView(viewModel)
        bottomLayoutVisible.set(true)
        return super.ItemViewModel(viewModel)
    }


    lateinit var curAmapLocation: AMapLocation
    var curPosition: Location? = null
    var isUp = false
    var curHeight = 0.0

    var last: Location? = null

    private fun location(amapLocation: AMapLocation?) {
        //处理定位信息
        if (amapLocation != null && amapLocation.errorCode == 0) {
            if (amapLocation?.gpsAccuracyStatus == AMapLocation.GPS_ACCURACY_BAD) {
                if (viewModel?.mapActivity?.onStart!!) {
                    CoroutineScope(uiContext).launch {
                        Toast.makeText(mapFr.activity, getString(R.string.gsp_bad), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            mapFr.mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
            if (curPosition == null) {
                viewModel?.mapActivity.dismissProgressDialog()
            }
            this.curAmapLocation = amapLocation
            this.curPosition = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName)
            if (driverStatus.get() == Drivering) {
                if (mapFr?.mapUtils?.breatheMarker_center != null) {
                    mapFr?.mapUtils?.breatheMarker_center!!.rotateAngle = amapLocation.bearing
                }
                if (viewModel.status.locationLat.size == 0) {

                    addStartPoint(curPosition!!)
                } else {
                    if (amapLocation.locationType == 1) {
                        if (curHeight < amapLocation.altitude) {
                            if (!isUp) {
                                isUp = true
                            }
                            viewModel?.status.UpValue += amapLocation.altitude - curHeight
                            viewModel?.status.UpCount++
                            if (curHeight > viewModel?.status.maxHeight) {
                                viewModel?.status.maxHeight = curHeight
                            }
                        } else {
                            //爬坡结束
                            if (isUp) {
                                isUp = false
                            }
                        }
                        curHeight = amapLocation.altitude
                        if (amapLocation.accuracy <= 30 && amapLocation.speed > 1) {
                            if (viewModel?.status.maxSpeed < amapLocation.speed) {
                                viewModel?.status.maxSpeed = amapLocation.speed
                            }
                            if (last == null) {
                                last = viewModel?.status.driverStartPoint!!
                            }
                            viewModel?.status.distance += AMapUtils.calculateLineDistance(LatLng(last?.latitude!!, last?.longitude!!), LatLng(amapLocation?.latitude!!, amapLocation?.longitude!!))
                            last = this.curPosition!!
                            viewModel?.mapActivity.mapUtils!!.setLocation(last!!)
                            var distanceTv = ""
                            if (viewModel?.status!!.distance > 1000) {
                                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance / 1000) + "KM"
                            } else {
                                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance) + "M"
                            }
                            driverDistance.set(distanceTv)
                            viewModel?.mapActivity.mAmap.moveCamera(CameraUpdateFactory.changeLatLng(viewModel?.mapActivity?.mapUtils?.breatheMarker_center?.position))
                            viewModel.status.locationLat.add(curPosition!!)
                            driverController?.setLineDatas(viewModel?.status?.locationLat, getColor(R.color.line_color))

                        }
                    }
                }
            }
        }
    }

    var weatherDatas = ObservableArrayList<WeatherEntity>().apply {
        for (i in 0..24) {
            if (i < 10) {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("0$i:00"), ObservableField("14℃")))
            } else {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("$i:00"), ObservableField("14℃")))
            }
        }
    }

    private fun addStartPoint(amapLocation: Location) {
        mapFr.dismissProgressDialog()
        mapFr.setDriverStyle()
        mapFr.mAmap.clear()
        viewModel?.status?.locationLat?.add(amapLocation)
        mapFr?.mapUtils?.createAnimationMarker(true, LatLonPoint(amapLocation.latitude, amapLocation.longitude))
//        mapActivity.getMapPointFragment().viewModel?.mapPointController?.startMaker = mapFr?.mapUtils!!.createMaker(amapLocation)
        driverController?.startMarker = mapFr?.mapUtils!!.createMaker(amapLocation)
        if (amapLocation?.aoiName != null && !amapLocation?.aoiName.isEmpty()) {
            viewModel?.status?.startAoiName = amapLocation?.aoiName
        } else {
            viewModel?.status?.startAoiName = amapLocation?.poiName
        }

        viewModel?.status?.driverStartPoint = Location(amapLocation.latitude, amapLocation.longitude, amapLocation.time.toString(), amapLocation.speed, amapLocation.height, amapLocation.bearing)

        UpdateDriverStatus(viewModel?.status!!)
    }

    private fun initView(viewModel: MapFrViewModel) {
        //进入方式判断
        if (viewModel?.status.driverStartPoint != null) {
            Observable.create(ObservableOnSubscribe<DriverDataStatus> {
                viewModel?.mapActivity.mAmap.clear()
                driverController?.startMarker = this.viewModel?.mapActivity.mapUtils?.createMaker(Location(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
//            viewModel?.driverController?.startMarker = mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude))
            })
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.item_start_navagation -> {
                //开始骑行
                if (driverStatus.get() == Drivering) {
                    driverStatus.set(DriverPause)
                    viewModel.status.startDriver.set(DriverPause)
                } else if (driverStatus.get() == DriverCancle || driverStatus.get() == TeamModel) {
                    if (viewModel.status.locationLat.size == 0) {
                        if (curPosition != null) {
                            addStartPoint(curPosition!!)
                            startDriver(0)
                        } else {
                            return
                        }
                    }
                }
            }
            R.id.item_long_press_btn -> {
                //取消骑行

            }
            R.id.item_continue_drivering -> {
                //继续骑行
                driverStatus.set(Drivering)
                viewModel.status.startDriver.set(Drivering)
            }
        }
    }

    var driverType = 0
    fun startDriver(type: Int) {
        this.driverType = type
        //开始骑行逻辑操作
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.mapActivity.showProgressDialog(getString(R.string.start_driver))
        HttpRequest.instance.startDriver = this
        HttpRequest.instance.startDriver(HashMap())
    }


    fun dontHaveOneMetre(cotent: String, leftBtnTv: String, rightBtnTv: String, type: Int) {
        var dia = DialogUtils.createNomalDialog(mapFr.activity!!, cotent, leftBtnTv, rightBtnTv)
        dia.setOnBtnClickL(OnBtnClickL {
            dia.dismiss()
            if (BaseApplication.MinaConnected!!) {
                var fr = viewModel?.items[1] as TeamItemModel
                fr?.endTeam(true)
                if (viewModel?.status.distance > 1000) {
                    driverController.driverOver()
                } else {
                    deleteDriverStatus(PreferenceUtils.getString(context, USERID))
                    cancleDriver(true)
                }
            } else {
                deleteDriverStatus(PreferenceUtils.getString(context, USERID))
                cancleDriver(true)
            }
            dia.dismiss()

        }, OnBtnClickL {
            if (BaseApplication?.MinaConnected!!) {
                var fr = viewModel?.items[1] as TeamItemModel
                if (type == 1 || type == 4) {
                    ARouter.getInstance().build(RouterUtils.TeamModule.TEAMER_PASS).withSerializable(RouterUtils.TeamModule.TEAM_INFO, fr?.TeamInfo).navigation()
                }
            }
            dia.dismiss()
        })
        dia.show()
    }

    var isResultPoint = false

    fun setResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_POINT -> {
                if (data!!.extras != null) {
                    if (data?.extras!!["tip"] != null) {
                        var tip = data?.extras!!["tip"] as PoiItem
                        if (tip != null) {
                            if (resultCode == RESULT_POINT) {
                                if (tip.latLonPoint?.latitude != null && tip.latLonPoint?.longitude != null) {
                                    mapFr?.mAmap?.isMyLocationEnabled = false
                                    isResultPoint = true
                                    mapFr?.mAmap?.moveCamera(CameraUpdateFactory.changeLatLng(AMapUtil.convertToLatLng(tip.latLonPoint)))
                                    var opotion = MarkerOptions().title(tip.title).snippet(getString(R.string.go_there)).position(LatLng(tip.latLonPoint.latitude, tip.latLonPoint.longitude))
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    var makder = mapFr?.mAmap?.addMarker(opotion)
                                    makder?.showInfoWindow()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onComponentFinish() {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, 0).navigation(mapFr.activity, RESULT_POINT)
    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun cancleDriver(b: Boolean) {
        driverStatus.set(DriverCancle)
        if (b) {
            mapFr.mAmap.clear()
        }
        driverController.cancleDriver()
        timerDispose?.dispose()
        timerDispose = null
        timer = null
        mapFr.mLocationOption?.isSensorEnable = false
        mapFr.mlocationClient?.setLocationOption(mapFr.mLocationOption)
        mapFr.myLocationStyle.showMyLocation(true)
        mapFr.mAmap.isMyLocationEnabled = true
        mapFr.mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        mapFr.mAmap.myLocationStyle = mapFr.myLocationStyle
        driverDistance.set("00:00")
        RxBus.default?.post("DriverCancle")
        viewModel?.status.reset()
    }


}