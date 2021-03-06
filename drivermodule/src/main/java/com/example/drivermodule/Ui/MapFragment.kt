package com.example.drivermodule.Ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.model.AMapCalcRouteResult
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.BR
import com.example.drivermodule.CalculateRouteListener
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.Controller.MapPointItemModel
import com.example.drivermodule.Controller.RoadBookItemModel
import com.example.drivermodule.Controller.TeamItemModel
import com.example.drivermodule.R
import com.example.drivermodule.Utils.MapControllerUtils
import com.example.drivermodule.Utils.MapUtils
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.example.drivermodule.databinding.FragmentMapBinding
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID


@Route(path = RouterUtils.MapModuleConfig.MAP_FR)
class MapFragment : BaseFragment<FragmentMapBinding, MapFrViewModel>(), LocationSource, AMap.InfoWindowAdapter, AMap.OnMapClickListener, AMap.OnMapTouchListener, AMap.OnInfoWindowClickListener, CalculateRouteListener {
    override fun CalculateCallBack(p0: AMapCalcRouteResult) {
        if (viewModel?.currentPosition == 2) {
            var model = viewModel?.items!![2] as RoadBookItemModel
            model.CalculateCallBack(p0)
        } else if (viewModel?.currentPosition == 3) {
            var model = viewModel?.items!![3] as MapPointItemModel
            model.CalculateCallBack(p0)
        }
    }

    var onStart = false
    var mLocationOption: AMapLocationClientOption? = null
    var mListener: LocationSource.OnLocationChangedListener? = null

    var NavigationStart = false

    lateinit var user: UserInfo
    lateinit var token: String
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient?.stopLocation()
            mlocationClient?.onDestroy()
        }
        mlocationClient = null
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(org.cs.tec.library.Base.Utils.context)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            //设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mLocationOption?.interval = 1000
            mLocationOption?.isLocationCacheEnable = true
            mLocationOption?.isSensorEnable = true
            mLocationOption?.isGpsFirst = false
            mLocationOption?.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport
            AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP)
//            mLocationOption?.isOnceLocationLatest = true
            mlocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient?.stopLocation()
            mlocationClient?.startLocation()
        }
    }

    override fun getInfoContents(maker: Marker?): View {
        if (viewModel?.currentPosition == 0 || viewModel?.currentPosition == 3) {
            Log.e("result", "getInfoContents" + maker!!.title)
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                var view = layoutInflater?.inflate(R.layout.popuwindow_custom, null, false)
                custionView(maker, view)
                return view!!
            }
        } else if (viewModel?.currentPosition == 1) {
            var model = viewModel?.items!![1] as TeamItemModel
            var flag = model.markerList?.containsValue(maker)
            if (flag) {
                var view = layoutInflater?.inflate(R.layout.team_popuwindow_custom, null, false)
                model.custionView(maker, view)
                return view!!
            } else {
                return null!!
            }

        } else if (viewModel?.currentPosition == 2) {
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                var model = viewModel?.items!![2] as RoadBookItemModel
                var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
                model.customView(maker, view)
                return view!!
            }
        }
        return null!!
    }

    override fun getInfoWindow(maker: Marker?): View {
        if (viewModel?.currentPosition == 0 || viewModel?.currentPosition == 3) {
            Log.e("result", "getInfoWindow" + maker!!.title)
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                var view = layoutInflater?.inflate(R.layout.popuwindow_custom, null, false)
                custionView(maker, view)
                return view!!
            }
        } else if (viewModel?.currentPosition == 1) {
            var model = viewModel?.items!![1] as TeamItemModel
            var flag = model.markerList?.containsValue(maker)
            if (flag) {
                var view = layoutInflater?.inflate(R.layout.team_popuwindow_custom, null, false)
                model.custionView(maker, view)
                return view!!
            } else {
                return null!!
            }

        } else if (viewModel?.currentPosition == 2) {
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                var model = viewModel?.items!![2] as RoadBookItemModel
                var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
                model.customView(maker, view)
                return view!!
            }
        }
        return null!!
    }

    private fun custionView(maker: Marker?, view: View?) {
        var title = maker?.title.toString()
        var ti = view?.findViewById<TextView>(R.id.window_title)
        ti?.text = title
        var type = view?.findViewById<TextView>(R.id.navigation_type)
        type?.text = maker?.snippet.toString()
    }

    override fun onMapClick(p0: LatLng?) {
        var fr = viewModel?.items!![1] as TeamItemModel
        fr?.MapClick(p0)
    }


    fun setDriverStyle() {
        mLocationOption?.isSensorEnable = true
        mlocationClient?.setLocationOption(mLocationOption)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        myLocationStyle.showMyLocation(false)
        mAmap.myLocationStyle = myLocationStyle
    }

    override fun onTouch(p0: MotionEvent?) {

    }

    override fun onInfoWindowClick(it: Marker?) {
        Log.e("result", "onInfoWindowClick")
        if (viewModel?.currentPosition == 0) {
            var model = viewModel?.items!![0] as DriverItemModel
            model.onInfoWindowClick(it)
        } else if (viewModel?.currentPosition == 1) {
            var model = viewModel?.items!![1] as TeamItemModel
            model.onInfoWindowClick(it)
        } else if (viewModel?.currentPosition == 2) {
            var model = viewModel?.items!![2] as RoadBookItemModel
            model.onInfoWindowClick(it)
        } else if (viewModel?.currentPosition == 3) {
            var model = viewModel?.items!![3] as MapPointItemModel
            model.onInfoWindowClick(it)
        }
    }


    var hotData: HotData? = null
    var resume: String = "nomal"

    override fun initContentView(): Int {
        return R.layout.fragment_map
    }

    override fun initVariableId(): Int {
        return BR.map_fr_Model
    }

    override fun initData() {
        super.initData()
        initStatus()
    }

    private fun initStatus() {
        user = queryUserInfo(PreferenceUtils.getString(context, USERID))[0]
        token = PreferenceUtils.getString(context, USER_TOKEN)
        var statusList = queryDriverStatus(user.data?.memberId!!)
        if (statusList.isNullOrEmpty()) {
            viewModel?.status = DriverDataStatus()
            viewModel?.status?.uid = user.data?.memberId!!
        } else {
            var status = queryDriverStatus(PreferenceUtils.getString(context, USERID))[0]
            viewModel?.status = status
        }
    }

    var mapUtils: MapControllerUtils? = null
    lateinit var mAmap: AMap
    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        fr_map_view.onCreate(savedInstanceState)
        Utils.setStatusTextColor(true, activity)
        mAmap = fr_map_view.map
        viewModel?.inject(this)
        setUpMap()
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        initListener()
    }

    private fun initListener() {
        mAmap.uiSettings.isZoomControlsEnabled = false
        mAmap.uiSettings.isMyLocationButtonEnabled = false
        mAmap.setInfoWindowAdapter(this)
        mAmap.setOnMarkerDragListener(viewModel)
        mAmap.setOnCameraChangeListener(viewModel)
        mAmap.setOnMarkerClickListener(viewModel)
        mAmap.setOnMapClickListener(this)
        mAmap.setOnMapTouchListener(this)
        mAmap.setOnInfoWindowClickListener(this)
        mapUtils = MapControllerUtils(this)
        mapUtils?.caculateRouteListener = this
    }

    fun getDrverFragment(): DriverFragment {
        return viewModel?.mFragments!![0] as DriverFragment
    }

    fun getTeamFragment(): TeamFragment {
        return viewModel?.mFragments!![1] as TeamFragment
    }

    fun getMapPointFragment(): MapPointFragment {
        return viewModel?.mFragments!![2] as MapPointFragment
    }

    fun getRoadBookFragment(): RoadBookFragment {
        return viewModel?.mFragments!![3] as RoadBookFragment
    }

    fun getDrverController(): DriverItemModel {
        return viewModel?.items!![0] as DriverItemModel
    }

    fun getTeamController(): TeamItemModel {
        return viewModel?.items!![1] as TeamItemModel
    }

    fun getMapPointController(): MapPointItemModel {
        return viewModel?.items!![3] as MapPointItemModel
    }

    fun getRoadBookController(): RoadBookItemModel {
        return viewModel?.items!![2] as RoadBookItemModel
    }

    private fun setUpMap() {
        mAmap.setLocationSource(this)// 设置定位监听
        mAmap.uiSettings.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        mAmap.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle()
    }


    override fun onPause() {
        super.onPause()
        fr_map_view.onPause()
        onStart = false
    }

    override fun onResume() {
        super.onResume()
        fr_map_view.onResume()
        onStart = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fr_map_view != null) {
            fr_map_view.onDestroy()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (fr_map_view != null) {
            fr_map_view.onSaveInstanceState(outState)
        }
    }

    var mlocationClient: AMapLocationClient? = null
    lateinit var myLocationStyle: MyLocationStyle
    fun setupLocationStyle() {
        // 自定义系统定位蓝点
        myLocationStyle = MyLocationStyle()
        // 自定义定位蓝点图标


        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked))
        myLocationStyle.radiusFillColor(Color.parseColor("#303FC5C9"))
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT)
        //自定义精度范围的圆形边框宽度
        // 设置圆形的填充颜色
//        myLocationStyle.radiusFillColor(Color.TRANSPARENT)
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAmap.myLocationStyle = myLocationStyle
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)

        Log.e("result", "onFragmentResult")

        if (requestCode == REQUEST_CREATE_JOIN) {
            viewModel?.changerFragment(1)
            (viewModel?.items!![1] as TeamItemModel).doCreate(data)
        } else {
            if (requestCode == RESULT_POINT) {
                (viewModel?.items!![0] as DriverItemModel).setResult(requestCode, resultCode, data)
            } else if (requestCode == ROAD_DETAIL_RETURN_VALUE) {

            } else {
                if (requestCode == REQUEST_LOAD_ROADBOOK) {
                    if (data?.getSerializable("hotdata") != null) {
                        var its = data.getSerializable("hotdata") as HotData
                        viewModel?.changerFragment(2)
                        (viewModel?.items!![2] as RoadBookItemModel).doLoadDatas(its!!)
                    } else {
                        if (viewModel?.currentPosition != 2) {
                            viewModel?.selectTab(viewModel?.currentPosition!!)
                        }
                    }
                } else {
                    0
                    (viewModel?.items!![3] as MapPointItemModel).SearchResult(requestCode, resultCode, data)
                }
            }

        }
    }

    fun setDriverStatus(resume: String?) {
        if (resume != null) {
            this.resume = resume
        }
    }


}