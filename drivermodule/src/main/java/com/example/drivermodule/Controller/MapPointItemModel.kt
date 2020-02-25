package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableField
import android.view.View
import com.amap.api.maps.model.Marker
import com.amap.api.services.core.LatLonPoint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.elder.zcommonmodule.Entity.Location
import com.example.drivermodule.Adapter.AddPointAdapter
import com.example.drivermodule.Adapter.AddPointItemAdapter
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.zk.library.Base.ItemViewModel
import org.cs.tec.library.Base.Utils.getString


class MapPointItemModel : ItemViewModel<MapFrViewModel>(), BaseQuickAdapter.OnItemClickListener {
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    //地图选点逻辑处理
    var startMaker: Marker? = null
    var screenMaker: Marker? = null
    var dataEmpty = ObservableField<Boolean>(false)
    var finalyText = ObservableField<String>(getString(R.string.location_select))
    var choiceVisible = ObservableField<Boolean>(false)
    var pointList = ArrayList<PointEntity>()
    lateinit var adapter  : AddPointItemAdapter
    var SingleList = ArrayList<PointEntity>().apply {
        this.add(PointEntity("", LatLonPoint(0.0, 0.0)))
    }
    fun SearchResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    fun onComponentFinish() {
    }


    fun onInfoWindowClick(it: Marker?) {

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
        mapFr.mapUtils?.queryGeocoder(LatLonPoint(viewModel?.status.navigationStartPoint!!.latitude, viewModel?.status.navigationStartPoint!!.longitude))
    }

    fun onClick(view: View) {

    }

}