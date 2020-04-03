package com.elder.zcommonmodule.Widget.RoadBook

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elder.zcommonmodule.BR
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.Widget.LoginUtils.BaseDialogFragment
import com.elder.zcommonmodule.databinding.FragmentRoadBookDialogBinding
import kotlinx.android.synthetic.main.fragment_road_book_dialog.*


class RoadBookHomeFragment : BaseDialogFragment() {

    lateinit var model: RoadBookHomeViewModel

    var type = 0

    var location: Location? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding = DataBindingUtil.inflate<FragmentRoadBookDialogBinding>(inflater, R.layout.fragment_road_book_dialog, container, false)
        model = RoadBookHomeViewModel()
        binding.setVariable(BR.road_book_home_dialog_model, model)
        binding.executePendingBindings()
        mContentView = binding.root
        return mContentView
    }

    fun setLocation(location: Location): RoadBookHomeFragment {
        this.location = location
        return this@RoadBookHomeFragment
    }

    fun setType(type: Int): RoadBookHomeFragment {
        this.type = type
        return this@RoadBookHomeFragment
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_road_book_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(dialog_mTabLayout))
        dialog_mTabLayout.setupWithViewPager(dialog_road_book_viewpager)
        dialog_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        dialog_swipe.setOnRefreshListener(model)
        model?.inject(this)
    }

}