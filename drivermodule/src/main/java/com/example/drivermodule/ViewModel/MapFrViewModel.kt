package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.example.drivermodule.R
import com.zk.library.Base.BaseViewModel
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Drivering
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Even.RxBusEven
import com.elder.zcommonmodule.Inteface.Locationlistener
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.Controller.MapPointItemModel
import com.example.drivermodule.Controller.RoadBookItemModel
import com.example.drivermodule.Controller.TeamItemModel
import com.example.drivermodule.Ui.*
import com.google.gson.Gson
import com.zk.library.Base.ItemViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_road_book.*
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import java.util.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID


class MapFrViewModel : BaseViewModel(), AMap.OnMarkerClickListener, AMap.OnMarkerDragListener, AMap.OnCameraChangeListener, TitleComponent.titleComponentCallBack, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, DriverComponent.onFiveClickListener {
    override fun FiveBtnClick(view: View) {


        Log.e("result", "点击事件点急急急急")
        if (currentPosition == 0) {
            (items[0] as DriverItemModel).onFiveBtnClick(view)
        } else if (currentPosition == 2) {
//            mapActivity.getMapPointFragment().viewModel?.FiveBtnClick(view)
        } else if (currentPosition == 3) {
//            mapActivity.getRoadBookFragment().viewModel?.FiveBtnClick(view)
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (p0!!.position == currentPosition) {
            return
        }
        if (p0!!.position == 2 && currentPosition == 3) {
            return
        }
        when (p0?.position) {
            0 -> {
                if (currentPosition == 1) {
                    mapActivity.getTeamFragment().viewModel?.backToDriver()

                } else if (currentPosition == 3) {
                    mapActivity.getRoadBookFragment().viewModel?.backToDriver()
                    changerFragment(0)
                }
            }
            1 -> {
                if (currentPosition == 0) {
                    //跳转到组队
                    mapActivity.getDrverFragment().viewModel?.GoTeam()
                } else if (currentPosition == 3) {
                    mapActivity.getRoadBookFragment().viewModel?.backToDriver()
                    mapActivity.getDrverFragment().viewModel?.GoTeam()
                }
            }
            2 -> {
                if (currentPosition == 0 || currentPosition == 1) {
                    //跳转到路书
                    if (currentPosition == 1) {
                        mapActivity.getTeamFragment().viewModel?.backToRoad()
                    }
                    if (mapActivity.getRoadBookFragment().viewModel?.netWorkData == null) {
                        var date = PreferenceUtils.getString(mapActivity.activity, PreferenceUtils.getString(context, USERID) + "hot")
                        if (date == null && mapActivity.hotData == null) {
                            if (mapActivity.getDrverFragment().curPosition != null) {
                                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE, 1).withSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, mapActivity.getDrverFragment().curPosition).navigation(mapActivity.activity, REQUEST_LOAD_ROADBOOK)
                            }
                        } else {
                            if (date == null) {
                                mapActivity.getRoadBookFragment().viewModel?.data = mapActivity.hotData
                            } else {
                                mapActivity.getRoadBookFragment().viewModel?.data = Gson().fromJson<HotData>(date, HotData::class.java)
                            }
                            changerFragment(3)
                            mapActivity.getRoadBookFragment().viewModel?.doLoadDatas(mapActivity.getRoadBookFragment().viewModel?.data!!)

                        }
                    } else {
                        changerFragment(3)
                        if (mapActivity.getRoadBookFragment().road_tab.selectedTabPosition != 0) {
                            mapActivity.getRoadBookFragment().viewModel!!.selectTab(0)
                        } else {
                            mapActivity.getDrverFragment()?.viewModel?.recycleComponent?.initDatas(mapActivity.getRoadBookFragment().viewModel?.netWorkData!!, mapActivity.getRoadBookFragment().viewModel?.data, 0)
                        }
                    }
                }
            }
        }
    }

    lateinit var status: DriverDataStatus
    //    var driverController = DriverController(this)
    var cur = 0L

    override fun onComponentClick(view: View) {

        if (currentPosition == 3) {

        } else {
            var even = RxBusEven()
            even.type = RxBusEven.DriverReturnRequest
            RxBus.default?.post(even)
        }
        return

        if (currentPosition == 0) {
            mapActivity.getDrverFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 1) {
            mapActivity.getTeamFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 2) {
            mapActivity.getMapPointFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 3) {
            mapActivity.getRoadBookFragment().viewModel?.onComponentClick(view)
        }
    }


    override fun onComponentFinish(view: View) {
        if (currentPosition == 0) {
            (items[0] as DriverItemModel).onComponentFinish()
        } else if (currentPosition == 1) {
            (items[0] as TeamItemModel).onComponentFinish()
        } else if (currentPosition == 3) {
            (items[0] as MapPointItemModel).onComponentFinish()
        } else if (currentPosition == 2) {
            (items[0] as RoadBookItemModel).onComponentFinish()
        }
    }


    var adapter = BindingViewPagerAdapter<ItemViewModel<MapFrViewModel>>()

    var items = ObservableArrayList<ItemViewModel<MapFrViewModel>>()


    var itemBinding = ItemBinding.of<ItemViewModel<MapFrViewModel>> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.driver_item, R.layout.itemmodel_driver)
            }
            1 -> {
                itemBinding.set(BR.team_item, R.layout.itemmodel_team)
            }
            2 -> {
                itemBinding.set(BR.roadbook_item, R.layout.itemmodel_roadbook)
            }
            3 -> {
                itemBinding.set(BR.map_point_item, R.layout.itemmodel_map_point)
            }
        }
    }


    override fun onCameraChangeFinish(p0: CameraPosition?) {
        RxBus.default?.post("onCameraChangeFinish")
    }

    override fun onCameraChange(p0: CameraPosition?) {
    }

    override fun onMarkerDragEnd(p0: Marker?) {
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.e("result", "onMarkerClick")
        when (currentPosition) {
            3 -> {
//                mapActivity.getRoadBookFragment().viewModel?.markerChange(p0)
            }
        }
        return false
    }


    var component = DriverComponent()


    var showBottomSheet = ObservableField<Boolean>(false)
    var mFragments = ArrayList<Fragment>()
    lateinit var mapActivity: MapFragment
    var a: Disposable? = null
    fun inject(mapActivity: MapFragment) {
        this.mapActivity = mapActivity
        var pos = ServiceEven()
        pos.type = "HomeDriver"
        RxBus.default?.post(pos)
        component.setHomeStyle()
        items.apply {
            this.add(DriverItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(TeamItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(RoadBookItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(MapPointItemModel().ItemViewModel(this@MapFrViewModel))
        }
        a = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            if (listeners != null) {
                listeners?.onLocation(it)
            }
        }
        RxSubscriptions.add(a)
        component.setCallBack(this)
        component.setOnFiveClickListener(this)
        initTab()
//        changerFragment(0)
    }


    var listeners: Locationlistener? = null

    lateinit var tab: TabLayout
    private fun initTab() {
        tab = mapActivity.binding?.root!!.findViewById<TabLayout>(R.id.topTab)
        tab.addTab(tab.newTab().setText(getString(R.string.driver)))
        tab.addTab(tab.newTab().setText(getString(R.string.team)))
        tab.addTab(tab.newTab().setText(getString(R.string.road_book_nomal_title)))
        tab.addOnTabSelectedListener(this)
    }


    fun selectTab(position: Int) {
        var tabs = tab.getTabAt(position)
        tabs?.select()
    }

    var currentPosition = 0

    fun changerFragment(position: Int) {
        currentPosition = position
        if (position == 2) {
            component.Drivering.set(false)
            component.rightIcon.set(context.getDrawable(R.drawable.three_point))
        } else {
            component.Drivering.set(true)
            component.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
        }
        if (position == 3) {
            component.titleVisible.set(true)
            tab.visibility = View.GONE
            component.rightVisibleType.set(true)
            component.title.set(getString(R.string.location_select))
            component.rightText.set(getString(R.string.road_detail))
            component.type.set(0)
        } else {
            component.titleVisible.set(false)
            component.rightVisibleType.set(false)
            component.title.set("")
            tab.visibility = View.VISIBLE
            component.rightText.set("")
            component.type.set(1)
        }
        mapActivity.fr_main_rootlay.currentItem = currentPosition
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.fr_share_btn -> {
                var fr = mapActivity.getDrverFragment()
                showBottomSheet.set(false)
                RxBus.default?.postSticky(fr?.viewModel?.share!!)
                fr.behaviors.isHideable = true
                fr.behaviors.state = BottomSheetBehavior.STATE_EXPANDED
                a?.dispose()
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.SHARE_ACTIVITY).navigation(mapActivity.activity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        }
    }

    fun startNavi(navigationStartPoint: Location, navigationEndPoint: Location, wayPoint: ArrayList<LatLng>, b: Int) {
        if (navigationEndPoint != null && navigationStartPoint != null) {
            status.navigationType = 1
            var list = ArrayList<LatLng>()
            list.add(LatLng(navigationStartPoint.latitude, navigationStartPoint.longitude))
            if (wayPoint != null && wayPoint.size != 0) {
                wayPoint.forEach {
                    list.add(it)
                }
            }
            list.add(LatLng(navigationEndPoint.latitude, navigationEndPoint.longitude))
            if (status.startDriver.get() != Drivering) {
                status.startDriver.set(Drivering)
                (items[0] as DriverItemModel).driverStatus.set(Drivering)
            }
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.NAVIGATION)
                    .withSerializable(RouterUtils.MapModuleConfig.NAVIGATION_DATA, list)
                    .withInt(RouterUtils.MapModuleConfig.NAVIGATION_TYPE, status.navigationType)
                    .withFloat(RouterUtils.MapModuleConfig.NAVIGATION_DISTANCE, status.navigationDistance)
                    .withLong(RouterUtils.MapModuleConfig.NAVIGATION_TIME, status.navigationTime)
                    .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                    .navigation()
        }

    }
}