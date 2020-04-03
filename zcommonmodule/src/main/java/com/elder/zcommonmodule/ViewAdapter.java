package com.elder.zcommonmodule;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.elder.zcommonmodule.Entity.HotData;
import com.elder.zcommonmodule.Widget.RoadBook.FrHotRoadItemModle;
import com.elder.zcommonmodule.Widget.RoadBook.FrNearRoadItemModle;
import com.zk.library.Base.BaseApplication;
import com.zk.library.Utils.PreferenceUtils;

import org.cs.tec.library.Utils.ConvertUtils;

import java.text.SimpleDateFormat;

public class ViewAdapter {


    @BindingAdapter("android:imgUrlForPhoto")
    public static void setImageUri(final ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            String token = PreferenceUtils.getString(imageView.getContext(), ConfigKt.USER_TOKEN);
            String path = ConfigKt.Base_URL + "/AmoskiActivity/userCenterManager/getImg?imgUrl=" + url + "&appToken=" + token;
            RequestOptions options = new RequestOptions().error(com.zk.library.R.drawable.icon_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(300, 300);
            Glide.with(imageView.getContext()).asDrawable().load(path).apply(options).into(imageView);
        }
    }
    @BindingAdapter("StrategySetting")
    public static void StrategySetting(final RecyclerView recy, FrHotRoadItemModle url) {
        if (recy == null) {
            return;
        }
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recy.getLayoutManager();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }
    @BindingAdapter("StrageImageLoad")
    public static void StrageImageLoad(final ImageView relativeLayout, HotData url) {
        int width = url.getWidth();
        int height = url.getHeight();
        int realWidth = (BaseApplication.Companion.getInstance().getGetWidthPixels() - ConvertUtils.Companion.dp2px(10)) / 2;
        float scale = realWidth * 1F / width;
        String s = LocalUtilsKt.getRoadImgUrl(url.getBill());
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).timeout(3000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Glide.with(relativeLayout.getContext()).asBitmap().load(s).apply(options).into(relativeLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        params.height = (int) (height * scale);
        relativeLayout.setLayoutParams(params);
    }
    @BindingAdapter("StrategySettingNear")
    public static void StrategySettingNear(final RecyclerView recy, FrNearRoadItemModle url) {
        if (recy == null) {
            return;
        }
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recy.getLayoutManager();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }

    @BindingAdapter("setColorFilter")
    public static void setColorFilter(ImageView move, boolean flag) {
        if (flag) {
            move.setColorFilter(Color.WHITE);
        } else {
            move.setColorFilter(Color.BLACK);
        }
    }


}
