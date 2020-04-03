package com.elder.zcommonmodule.Widget.RoadBook

import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.R
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter


class FrStrageAdapter: BindingRecyclerViewAdapter<HotData>(){
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemView != null) {
            if (holder.itemView.findViewById<ImageView>(R.id.road_strage_img) != null) {
                var img = holder.itemView.findViewById<ImageView>(R.id.road_strage_img)
                Glide.with(img.context).clear(img)
            }
        }
    }
}