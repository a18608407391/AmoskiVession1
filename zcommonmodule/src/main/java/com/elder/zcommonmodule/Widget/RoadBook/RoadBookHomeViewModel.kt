package com.elder.zcommonmodule.Widget.RoadBook

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.BR
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_road_book_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.OnItemBind
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class RoadBookHomeViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        roadHomeActivity.dismiss()
    }

    override fun onComponentFinish(view: View) {
//        RxBus.default?.post(RxBusEven.getInstance(RxBusEven.ENTER_TO_SEARCH))
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation(roadHomeActivity.activity, REQUEST_LOAD_ROADBOOK)
    }

    var curItems = 0
    override fun onRefresh() {
        if (curItems == 0) {
            var model = items[0] as FrHotRoadItemModle
            model.initDatas(model.page)

        } else if (curItems == 1) {
            var model = items[1] as FrNearRoadItemModle
            model.initDatas(model.page)
        }
    }

    var pagerSelectCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            curItems = t
            Log.e("result", "curItems" + t)
            if (t == 1) {
                if ((items[1] as FrNearRoadItemModle).items.size == 0) {
                    (items[1] as FrNearRoadItemModle).initDatas((items[1] as FrNearRoadItemModle).page)
                }
            } else {
//                (items[0] as HotRoadItemModle).initDatas()
            }
        }
    })


    lateinit var roadHomeActivity: RoadBookHomeFragment

    fun inject(roadBookHomeFragment: RoadBookHomeFragment) {
        this.roadHomeActivity = roadBookHomeFragment
        CoroutineScope(uiContext).launch {
            delay(500)
            initTabLayoutChangeUI()
            (items[0] as FrHotRoadItemModle).initDatas((items[0] as FrHotRoadItemModle).page)
        }
    }

    var items = ObservableArrayList<ItemViewModel<RoadBookHomeViewModel>>().apply {
        this.add(FrHotRoadItemModle(this@RoadBookHomeViewModel, 0))
        this.add(FrNearRoadItemModle(this@RoadBookHomeViewModel, 1))
    }
    var titleComponent = TitleComponent().apply {
        this.title.set(getString(R.string.road_book_title))
        this.rightText.set("")
        this.rightVisibleType.set(false)
        this.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
        this.arrowVisible.set(false)
        this.setCallBack(this@RoadBookHomeViewModel)
    }

    private fun initTabLayoutChangeUI() {
        for (i in 0..roadHomeActivity.dialog_mTabLayout.tabCount) {
            val tab = roadHomeActivity.dialog_mTabLayout.getTabAt(i)
            tab?.customView = getTabView(i)
        }

        roadHomeActivity.dialog_mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = false
                textView.setTextColor(getColor(R.color.blackTextColor))
                indicator.visibility = View.INVISIBLE
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = true
                textView.setTextColor(getColor(R.color.line_color))
                indicator.visibility = View.VISIBLE
            }
        })
    }

    private fun getTabView(position: Int): View {
        val view = LayoutInflater.from(roadHomeActivity.activity).inflate(R.layout.fr_indicate_layout_tab, null)
        val textView = view.findViewById(R.id.tab_item_textview) as TextView
        val indicator = view.findViewById(R.id.view_music_indicator) as View
        textView.text = mTiltes[position]
        if (position == 0) {
            textView.paint.isFakeBoldText = true
            indicator.visibility = View.VISIBLE
        } else {
            indicator.visibility = View.INVISIBLE
        }
        return view
    }

    private val mTiltes = arrayOf(getString(R.string.hot_title), getString(R.string.nearly))
    var adapter = BindingViewPagerAdapter<ItemViewModel<RoadBookHomeViewModel>>()
    var itembingding = OnItemBind<ItemViewModel<RoadBookHomeViewModel>> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.hot_itemmodel, R.layout.fr_hot_item_model_layout)
            }
            1 -> {
                itemBinding.set(BR.near_itemmodel, R.layout.fr_near_item_model_layout)
            }
        }
    }


    var pagerTitle = BindingViewPagerAdapter.PageTitles<ItemViewModel<RoadBookHomeViewModel>> { position, item ->
        mTiltes[position]
    }
}