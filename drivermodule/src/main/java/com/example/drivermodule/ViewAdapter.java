package com.example.drivermodule;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.elder.zcommonmodule.Entity.DriverDataStatus;
import com.elder.zcommonmodule.Entity.HotData;
import com.elder.zcommonmodule.Entity.Location;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.elder.zcommonmodule.Entity.WeatherEntity;
import com.elder.zcommonmodule.Utils.URLImageParser;
import com.elder.zcommonmodule.Widget.Chart.SuitLines;
import com.elder.zcommonmodule.Widget.Chart.Unit;
import com.elder.zcommonmodule.Widget.CustomRecycleView;
import com.elder.zcommonmodule.Widget.LongPressToFinishButton;
import com.example.drivermodule.Adapter.AddPointAdapter;
import com.example.drivermodule.Adapter.AddPointItemAdapter;
import com.example.drivermodule.Entity.RoadBook.HotBannerData;
import com.example.drivermodule.ItemModel.HotRoadItemModle;
import com.example.drivermodule.ItemModel.NearRoadItemModle;
import com.example.drivermodule.Sliding.SlidingUpPanelLayout;
import com.zk.library.Base.BaseApplication;
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Utils.ConvertUtils;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jaydenxiao.com.expandabletextview.ExpandableTextView;

public class ViewAdapter {
    private static ArrayList<WeatherEntity> entitys;

    @BindingAdapter(value = {"android:HorizontalScrollViewDatas"})
    public static void loadImageByLinearLayout(LinearLayout layout, ArrayList<WeatherEntity> entity) {
        if (entitys == null) {
            entitys = entity;
            addView(layout, entity);
        } else {
            if (entitys != entity) {
                layout.removeAllViews();
                layout.invalidate();
                addView(layout, entity);
                entitys = entity;
            }
        }
    }


    @BindingAdapter("initBehavior")
    public static void setBehavior(LinearLayout layout, int b) {
        BottomSheetBehavior<LinearLayout> s = BottomSheetBehavior.from(layout);
        s.setState(b);
    }


    @BindingAdapter("initPanel")
    public static void initPanel(SlidingUpPanelLayout panel, SlidingUpPanelLayout.PanelState state) {
        panel.setPanelState(state);
    }

    @BindingAdapter("initMapPointRecycle")
    public static void initMapPointRecycle(CustomRecycleView view, AddPointItemAdapter adapter) {
        if (adapter == null) {
            return;
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(view.getContext());
        ItemDragAndSwipeCallback callback = new ItemDragAndSwipeCallback(adapter);
        callback.setDragMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(view);
        adapter.setOnItemDragListener(adapter);
        adapter.enableDragItem(helper,R.id.drag_layout, true);
        view.setLayoutManager(manager);
        view.setAdapter(adapter);
    }

    public static void addView(LinearLayout layout, final ArrayList<WeatherEntity> entity) {
        for (int i = 0; i < entity.size(); i++) {
            final int position = i;
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.horizontal_weather_child, layout, false);
            ImageView img = view.findViewById(R.id.weather_icon);
            TextView time = view.findViewById(R.id.weather_time);
            TextView temperature = view.findViewById(R.id.weather_temperature);
            img.setImageDrawable(entity.get(i).getIcon().get());
            time.setText(entity.get(i).getTime().get());
            temperature.setText(entity.get(i).getTime().get());
            layout.addView(view);
            layout.invalidate();
        }
    }


    @BindingAdapter("SuitLines")
    public static void initSuitLines(SuitLines chart, DriverDataStatus status) {
        if (status == null) {
            return;
        }
        ArrayList height = new ArrayList<Double>();
        long time = status.getSecond();
        ObservableArrayList<Location> locations = status.getLocationLat();
        for (int i = 0; i < locations.size(); i++) {
            height.add(locations.get(i));
        }
        if (height.isEmpty() || height.size() < 6) {
            return;
        }
        int k = 0;
        int size = height.size();
        if ((size - 1) % 15 != 0) {
            k = (size - 1) % 15;
        }
        int n = (size - k) / 15;
        if (n == 0) {
            return;
        }
        int baseTime = (int) (time / 15);
        int count = 0;
        List<Unit> lines = new ArrayList<>();
        for (int i = 0; i < size - 1; i++) {
            if (i % n == 0) {
                count++;
                Unit t = new Unit((float) (height.get(i)) + 1, (new DecimalFormat("0.0").format(baseTime * count) + "s"));
                lines.add(t);
            } else if (i == size - 1) {
                Unit t = new Unit((float) (height.get(i)) + 1, "");
                lines.add(t);
            } else {
                lines.add(new Unit((float) (height.get(i)) + 1));
            }
        }
        chart.feed(lines);
    }


    @BindingAdapter("initWeatherHori")
    public static void initWeatherHori(LinearLayout layout, ObservableArrayList<WeatherEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.horizontal_weather_child, layout, false);
            ImageView img = view.findViewById(R.id.weather_icon);
            TextView time = view.findViewById(R.id.weather_time);
            TextView temperature = view.findViewById(R.id.weather_temperature);
            img.setImageDrawable(list.get(i).getIcon().get());
            time.setText(list.get(i).getTime().get());
            temperature.setText(list.get(i).getTemperature().get());
            layout.addView(view);
            layout.invalidate();
        }

    }

    @BindingAdapter("AddHotDataView")
    public static void AddHotDataView(LinearLayout linear, final ArrayList<HotBannerData> entity) {
        linear.removeAllViews();
        for (int i = 0; i < entity.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) linear.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hot_horizontal_child_layout, linear, false);
            ImageView img = view.findViewById(R.id.hot_img);
            TextView tv = view.findViewById(R.id.hot_tv);
            linear.addView(view);
        }
        linear.invalidate();
    }


    @BindingAdapter("LoadRoadImg")
    public static void LoadRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(315), ConvertUtils.Companion.dp2px(168));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }


    @BindingAdapter("TextSpan")
    public static void TextSpan(TextView tv, String text) {

    }

    @BindingAdapter("LoadBitmapForbg")
    public static void loadBitmap(ImageView img, Bitmap bitmap) {
        if (bitmap != null) {
            RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
            RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(345), ConvertUtils.Companion.dp2px(215));
            Glide.with(img.getContext()).asBitmap().load(bitmap).apply(options).into(img);
        }
    }


    @BindingAdapter("LoadRoadBottomImg")
    public static void LoadRoadBottomImg(ImageView img, String bitmap) {
        if (bitmap != null) {
            RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
            RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.artboard).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(40), ConvertUtils.Companion.dp2px(40));
            Glide.with(img.getContext()).asBitmap().load(bitmap).apply(options).into(img);
        }
    }

    @BindingAdapter("HtmlText")
    public static void setHtmlText(TextView tv, String html) {
        if (html == null) {
            return;
        }
        if (html.contains(".jpg")) {
            tv.setText(Html.fromHtml(html, new URLImageParser(tv), null));
        } else {
            tv.setText(Html.fromHtml(html));
        }
    }

    @BindingAdapter("TeamHead")
    public static void loadTeamHead(ImageView img, String bitmap) {
        if (bitmap == "0") {
            img.setImageResource(R.drawable.team_first);
        } else if (bitmap == "1") {
            img.setImageResource(R.drawable.team_second);
        } else {
            CircleCrop crop = new CircleCrop();
            RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(55), ConvertUtils.Companion.dp2px(55));
            Glide.with(img.getContext()).asBitmap().load(bitmap).apply(options).into(img);
        }
    }

    @BindingAdapter("setLinearBackWhiteGround")
    public static void setLinearBackWhiteGround(LinearLayout layout, int type) {
        if (type == 0) {
            layout.setBackgroundResource(R.drawable.rectangle_shape1);
        } else if (type == 1) {
            layout.setBackgroundResource(R.drawable.rectangle_shape);
        } else if (type == 2) {
            layout.setBackgroundResource(R.drawable.rectangle_shape2);
        } else if (type == 3) {
            layout.setBackgroundResource(R.drawable.corner_dialog);
        }
    }

    @BindingAdapter("setImageIconByType")
    public static void setImageIconByType(ImageView img, int type) {
        switch (type) {
            case 1:
                img.setImageResource(R.drawable.ic_point_type1);
                break;
            case 2:
                img.setImageResource(R.drawable.ic_point_type2);
                break;
            case 3:
                img.setImageResource(R.drawable.ic_point_type3);
                break;
            case 4:
                img.setImageResource(R.drawable.ic_point_type4);
                break;
            case 5:
                img.setImageResource(R.drawable.ic_point_type5);
                break;
            case 6:
                img.setImageResource(R.drawable.ic_point_type6);
                break;
            case 7:
                img.setImageResource(R.drawable.ic_point_type7);
                break;
            case 8:
                img.setImageResource(R.drawable.ic_point_type8);
                break;
        }
    }


    @BindingAdapter("setLinearBackGround")
    public static void setLinearBack(LinearLayout layout, int color) {
        if (layout != null) {
            if (color == Color.TRANSPARENT) {
                layout.setBackground(UtilsKt.getContext().getResources().getDrawable(R.drawable.team_fr_bottom_bg));
            } else {
                layout.setBackground(UtilsKt.getContext().getResources().getDrawable(R.drawable.team_fr_bottom_bg));
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //什么都不处理
                }
            });
        }
    }

    @BindingAdapter(value = "addcar_local")
    public static void setCar(ImageView img, String path) {
        CircleCrop crop = new CircleCrop();
        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/Activity")) {
                path = LocalUtilsKt.getImageUrl(path);
            }
        }
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter("TextVisible")
    public static void setTextVisible(View tv, int mode) {
        if (mode == 0) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("TextVisibleBottom")
    public static void setTextVisibleBottom(View tv, int mode) {
        if (mode == 2) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("TextVisibleBottomNavigation")
    public static void setTextVisibleBottomNavigation(View tv, int mode) {
        if (mode == 2 || mode == 4) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }


    @BindingAdapter("MapBottoimChange")
    public static void setCurrentModelChange(ImageView img, int mode) {
        int i = img.getId();
        if (i == R.id.start_navagation) {
            if (mode == 0) {
                //Driving
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.driver_main_play);
            } else if (mode == 1) {
                img.setVisibility(View.GONE);
            } else if (mode == 0) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            } else if (mode == 2) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            } else if (mode == 3) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            }
        } else if (i == R.id.stop_navagation) {
            if (mode == 0 || mode == 2) {
                img.setVisibility(View.GONE);
            } else if (mode == 1) {
                img.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("MapBottoimItemChange")
    public static void setCurrentModelItemChange(ImageView img, int mode) {
        int i = img.getId();
        if (i == R.id.item_start_navagation) {
            if (mode == 0) {
                //Driving
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.driver_main_play);
            } else if (mode == 1) {
                img.setVisibility(View.GONE);
            } else if (mode == 0) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            } else if (mode == 2) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            } else if (mode == 3) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.start_driver);
            }
        } else if (i == R.id.stop_navagation) {
            if (mode == 0 || mode == 2) {
                img.setVisibility(View.GONE);
            } else if (mode == 1) {
                img.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("setExTextContent")
    public static void setExTextContent(ExpandableTextView ex, String content) {
        ex.setText(content);
    }


    @BindingAdapter("setTextBack")
    public static void setTextBack(TextView img, int mode) {
        if (mode == Color.parseColor("#2D3138")) {
            img.setBackgroundColor(Color.TRANSPARENT);
        } else if (mode == Color.parseColor("#62B297")) {
            img.setBackgroundResource(R.drawable.little_tv_color_corner);
        } else {
            img.setBackgroundResource(R.drawable.little_tv_color_corner_yellow);
        }
    }

    @BindingAdapter("StopNaviBtn")
    public static void setStop(LinearLayout img, int mode) {
        if (mode == 0 || mode == 2) {
            img.setVisibility(View.GONE);
        } else if (mode == 1) {
            img.setVisibility(View.VISIBLE);
        }
    }


    @BindingAdapter("initTabLayout")
    public static void initTabLayout(TabLayout tab, int count) {
        tab.removeAllTabs();
        for (int i = 0; i <= count; i++) {
            if (i == 0) {
                tab.addTab(tab.newTab().setText("全程"));
            } else {
                tab.addTab(tab.newTab().setText("Day" + i));
            }
        }
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


    @BindingAdapter("StrategySetting")
    public static void StrategySetting(final RecyclerView recy, HotRoadItemModle url) {
        if (recy == null) {
            return;
        }
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recy.getLayoutManager();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }


    @BindingAdapter("StrategySettingNear")
    public static void StrategySettingNear(final RecyclerView recy, NearRoadItemModle url) {
        if (recy == null) {
            return;
        }
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recy.getLayoutManager();
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }
}
