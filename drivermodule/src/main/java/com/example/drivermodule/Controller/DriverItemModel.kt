package com.example.drivermodule.Controller

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Color
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.insertDriverStatus
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Drivering
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Component.DriverItemController
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.google.gson.Gson
import com.zk.library.Base.ItemViewModel
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
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class DriverItemModel : ItemViewModel<MapFrViewModel>(), HttpInteface.startDriverResult {
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
        if (driverType == 1) {
        //未骑行导航


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
    var driverAvatar = ObservableField<String>()
    var share = ShareEntity()

    var driverStatus = ObservableField(DriverCancle)

    var locationDispose: Disposable? = null

    var mapFr: MapFragment

    var driverController = DriverItemController(this)

    init {
        viewModel.mapActivity.showProgressDialog(getString(R.string.location_loading))
        locationDispose = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            location(it)
        }
        RxSubscriptions.add(locationDispose)
        mapFr = viewModel?.mapActivity
        driverStatus.set(viewModel?.status.startDriver.get())
        initView()
    }


    lateinit var curAmapLocation: AMapLocation
    lateinit var curPosition: Location
    var isUp = false
    var curHeight = 0.0

    var last: Location? = null

    private fun location(amapLocation: AMapLocation?) {
        //处理定位信息
        if (mapFr.mListener != null && amapLocation != null && amapLocation.errorCode == 0) {
            if (amapLocation?.gpsAccuracyStatus == AMapLocation.GPS_ACCURACY_BAD) {
                if (viewModel?.mapActivity?.onStart!!) {
                    CoroutineScope(uiContext).launch {
                        Toast.makeText(mapFr.activity, getString(R.string.gsp_bad), Toast.LENGTH_SHORT).show()
                    }
                }
                mapFr.mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
                this.curAmapLocation = amapLocation
                this.curPosition = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing)
                if (driverStatus.get() == Drivering) {
                    if (mapFr?.mapUtils?.breatheMarker_center != null) {
                        mapFr?.mapUtils?.breatheMarker_center!!.rotateAngle = amapLocation.bearing
                    }
                    if (viewModel.status.locationLat.size == 0) {
                        viewModel?.status?.locationLat?.add(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
                        addStartPoint(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
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
                                    last = viewModel?.status.driverStartPoint
                                }
                                viewModel?.status.distance += AMapUtils.calculateLineDistance(LatLng(last?.latitude!!, last?.longitude!!), LatLng(amapLocation?.latitude!!, amapLocation?.longitude!!))
                                last = this.curPosition
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

    private fun initView() {
        //进入方式判断
        if (viewModel?.status.driverStartPoint != null) {
            Observable.create(ObservableOnSubscribe<DriverDataStatus> {
                viewModel?.mapActivity.mAmap.clear()
                driverController?.startMarker = viewModel?.mapActivity.mapUtils?.createMaker(Location(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
//            viewModel?.driverController?.startMarker = mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude))
            })
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.item_start_navagation -> {
                //开始骑行
            }
            R.id.item_long_press_btn -> {
                //取消骑行
            }
            R.id.item_continue_drivering -> {
                //继续骑行
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


    fun dontHaveOneMetre(string: String, string1: String, string2: String, i: Int) {

    }


}