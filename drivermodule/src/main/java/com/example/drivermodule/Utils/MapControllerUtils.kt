package com.example.drivermodule.Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.AlphaAnimation
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.AnimationSet
import com.amap.api.maps.model.animation.ScaleAnimation
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.route.DistanceResult
import com.amap.api.services.route.DistanceSearch
import com.amap.api.services.route.DriveRouteResult
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.RouteSearch.*
import com.amap.api.trace.LBSTraceClient
import com.amap.api.trace.TraceListener
import com.amap.api.trace.TraceLocation
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.FileUtils
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.CalculateRouteListener
import com.example.drivermodule.Component.DriverItemController
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.CustomNaviCallback
import com.example.drivermodule.Entity.BitMapWithPath
import com.example.drivermodule.R
import com.example.drivermodule.Ui.DriverFragment
import com.example.drivermodule.Ui.MapFragment
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.fragment_driver.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumesAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.Utils.ToastUtils
import org.cs.tec.library.WX_APP_ID
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MapControllerUtils : GeocodeSearch.OnGeocodeSearchListener, DistanceSearch.OnDistanceSearchListener, TraceListener, AMap.OnMapScreenShotListener, AMap.OnCameraChangeListener, CustomNaviCallback {

    var CurState = 0

    companion object {
        var OnShareState = 19900325
    }

    override fun onCameraChangeFinish(p0: CameraPosition?) {
        when (CurState) {
            OnShareState -> {
                activity.mAmap.getMapScreenShot(this@MapControllerUtils)
                activity.map_view.invalidate()
                CurState = 0
            }
        }
    }

    override fun onCameraChange(p0: CameraPosition?) {

    }

    override fun onMapScreenShot(p0: Bitmap?) {
        var k = Observable.just(p0!!).subscribeOn(Schedulers.io()).map(Function<Bitmap, String> {
            val s = SimpleDateFormat("yyyyMMddHHmmss")
            var p = Environment.getExternalStorageDirectory().path + "/big_" + s.format(Date()) + ".png"
            var fs = FileOutputStream(p)
            var t = it?.compress(Bitmap.CompressFormat.PNG, 100, fs)
            fs.flush()
            fs.close()
            return@Function p
        })
        var m = Observable.just(p0!!).subscribeOn(Schedulers.io()).map(Function<Bitmap, BitMapWithPath> {
            var map = BitMapWithPath()
            var width = it?.width
            var hight = it?.height
            var newhight = (hight!! / 2) - (ConvertUtils.dp2px(231F) / 2)
            var y = newhight
            var x = 0
            var newBitmap = Bitmap.createBitmap(it, x, y, width!!, ConvertUtils.dp2px(231F))
            val sdf = SimpleDateFormat("yyyyMMddHHmmss")
            var path = Environment.getExternalStorageDirectory().path + "/little_" + sdf.format(Date()) + ".png"
            var fos = FileOutputStream(path)
            var b = newBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            map.path = path
            map.newBit = newBitmap
            return@Function map
        })
        Observable.zip(k, m, object : BiFunction<String, BitMapWithPath, BitMapWithPath> {
            override fun apply(t1: String, t2: BitMapWithPath): BitMapWithPath {
                var file = FileUtils.writeTxtToFile(Gson().toJson(fr?.shareUpLoad), Environment.getExternalStorageDirectory().path + "/Amoski", "/UpLoad_location_File" + fr.viewModel?.status?.driverNetRecord?.id + ".txt")
                t2.file = file
                t2.BigPath = t1
                return t2
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Upload(it.BigPath!!, it.path!!, it.newBit!!, it.file!!)
        }
    }

    fun Upload(p: String, path: String, newBitmap: Bitmap, file: File) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        var id = PreferenceUtils.getString(context, USERID)
        var user = queryUserInfo(id)[0]
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
            var body = RequestBody.create(MediaType.parse("text/plain"), file)
            var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
            var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiRiding/ridingManage/ridingFileUpd" + "?rid=" + fr.viewModel?.status?.driverNetRecord?.id).post(part).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).doOnError {
            activity._mActivity?.dismissProgressDialog()
            showUpLoadDialog(p, path, newBitmap, file)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            var m = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
            if (m.code == 0) {
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
                    var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
                    mult.addFormDataPart("files", File(p).name, RequestBody.create(MediaType.parse("image/jpg"), File(p)))
                    mult.addFormDataPart("files", File(path).name, RequestBody.create(MediaType.parse("image/jpg"), File(path)))
                    var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiRiding/ridingManage/uploadRidingPic" + "?rid=" + fr.viewModel?.status?.driverNetRecord?.id).post(mult.build()).build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).doOnError {
                    activity._mActivity?.dismissProgressDialog()
                    showUpLoadDialog(p, path, newBitmap, file)
                }.observeOn(AndroidSchedulers.mainThread()).subscribe {
                    var res = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                    activity._mActivity?.dismissProgressDialog()
                    if (res.code == 0) {
                        fr?.share.apply {
                            this?.userInfo?.set(user)
                            this?.shareIcon = BitmapFactory.decodeFile(p)
                        }
                        fr?.driverAvatar?.set(getImageUrl(user.data?.headImgFile))
                        fr?.userName?.set(user.data?.name)
                        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        var d = Date(System.currentTimeMillis())
                        fr?.finishTime?.set(simple.format(d))
                        fr?.deivceInfo?.id?.set(fr.viewModel?.status?.driverNetRecord?.id.toString())
                        fr?.deivceInfo?.distance?.set(DecimalFormat("0.0").format(fr.viewModel?.status?.distance!! / 1000))
                        fr?.deivceInfo?.time?.set(ConvertUtils.formatTimeS(fr.viewModel?.status?.second!!))
                        fr?.deivceInfo?.date?.set(simple.format(d))
                        fr?.cancleDriver(true)
                        File(p).delete()
                        File(path).delete()
                    } else {
                        Toast.makeText(context, res.msg, Toast.LENGTH_SHORT).show()
                        if (m.code == 10009) {
                            var pass = PreferenceUtils.getString(context, USER_PASS)
                            if (user?.data?.password == null) {
                                //微信登录的  进行微信授权
                                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled()) {
                                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                                } else {
                                    var req = SendAuth.Req()
                                    req.transaction = "inValidate"
                                    req.scope = "snsapi_userinfo"
                                    req.state = "wechat_sdk_xb_live_state"//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
                                    BaseApplication.getInstance().mWxApi.sendReq(req)
                                }
                            } else {
                                //账号密码登录的，直接登录账号密码
                                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_PASSWORD).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).navigation()
                            }
                        } else {

                        }
                        showUpLoadDialog(p, path, newBitmap, file)
                    }
                }
            } else {
                if (m.code == 10009) {
                    var pass = PreferenceUtils.getString(context, USER_PASS)
                    if (user?.data?.password == null) {
                        //微信登录的  进行微信授权
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled()) {
                            Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                        } else {
                            var req = SendAuth.Req()
                            req.transaction = "inValidate"
                            req.scope = "snsapi_userinfo"
                            req.state = "wechat_sdk_xb_live_state"//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
                            BaseApplication.getInstance().mWxApi.sendReq(req)
                        }
                    } else {
                        //账号密码登录的，直接登录账号密码
                        ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_PASSWORD).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).navigation()
                    }
                } else {

                }
                Toast.makeText(context, m.msg, Toast.LENGTH_SHORT).show()
                showUpLoadDialog(p, path, newBitmap, file)
            }
        }
    }


    fun showUpLoadDialog(p: String, path: String, newBitmap: Bitmap, file: File) {
        var d = DialogUtils.createNomalDialog(fr.mapFr.activity!!, getString(R.string.upload_fail), getString(R.string.upload_cancle), getString(R.string.upload_again))
        d.show()
        d.setOnBtnClickL(OnBtnClickL {
            activity._mActivity?.finish()
            d.dismiss()
        }, OnBtnClickL {
            Upload(p, path, newBitmap, file)
            d.dismiss()
        })
    }


    override fun onMapScreenShot(p0: Bitmap?, p1: Int) {}
    override fun onRequestFailed(p0: Int, p1: String?) {
    }

    override fun onTraceProcessing(p0: Int, p1: Int, p2: MutableList<LatLng>?) {
    }

    override fun onFinished(p0: Int, p1: MutableList<LatLng>?, p2: Int, p3: Int) {
    }

    override fun onDistanceSearched(p0: DistanceResult?, p1: Int) {
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        if (p1 == 1000) {
            RxBus.default?.post(p0!!)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    var geocoderSearch: GeocodeSearch
    var fr: DriverItemModel
    var mRoutePath: RouteSearch
    var disSearch: DistanceSearch
    var disQuery: DistanceSearch.DistanceQuery
    var mTraceClient: LBSTraceClient
    var activity: MapFragment
    var navi: AMapNavi

    constructor(activty: MapFragment) {
        this.activity = activty
        geocoderSearch = GeocodeSearch(activty._mActivity)
        geocoderSearch.setOnGeocodeSearchListener(this)
        mRoutePath = RouteSearch(activty._mActivity)
        disSearch = DistanceSearch(context)
        disSearch.setDistanceSearchListener(this)
        disQuery = DistanceSearch.DistanceQuery()
        mTraceClient = LBSTraceClient(context)
        fr = activty.viewModel?.items!![0] as DriverItemModel
        navi = AMapNavi.getInstance(this.activity.activity)
    }


    fun queryDistance(vararg points: LatLonPoint) {
        disQuery.addOrigins(*points)
    }

    fun queryGeocoder(latLonPoint: LatLonPoint) {
        var query = RegeocodeQuery(latLonPoint, 200F,
                GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query)// 设置异步逆地理编码请求
        activity.getMapPointController()?.screenMaker?.hideInfoWindow()
    }


    var breatheMarker: Marker? = null
    var breatheMarker_center: Marker? = null

    fun setLocation(location: Location) {
        breatheMarker?.position = LatLng(location.latitude, location.longitude)
        breatheMarker_center?.position = LatLng(location.latitude, location.longitude)
    }

    fun clearMarker() {
        if (breatheMarker != null) {
            breatheMarker?.remove()
            breatheMarker_center?.remove()
            breatheMarker = null
            breatheMarker_center = null
        }
    }

    fun createAnimationMarker(flag: Boolean, end: LatLonPoint): Marker? {
        if (!flag) {
//            Log.e("result", "关闭定位")
            activity.mAmap.isMyLocationEnabled = false
        }
        // 中心的marker
        var bit = BitmapFactory.decodeResource(activity.resources, R.drawable.green_circle)
        breatheMarker = activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(1f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.myself_inner)))
        // 中心的marker
        breatheMarker_center = activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(2f)
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))

        // 动画执行完成后，默认会保持到最后一帧的状态
        var animationSet = AnimationSet(true)
        var alphaAnimation = AlphaAnimation(0.5f, 0f)
        alphaAnimation.setDuration(2000)
        // 设置不断重复
        alphaAnimation.repeatCount = Animation.INFINITE
        var scaleAnimation = ScaleAnimation(1f, 2.5f, 1f, 2.5f)
        scaleAnimation.setDuration(2000)
        // 设置不断重复
        scaleAnimation.repeatCount = Animation.INFINITE
        animationSet.addAnimation(alphaAnimation)
        animationSet.addAnimation(scaleAnimation)
        animationSet.setInterpolator(LinearInterpolator())
        breatheMarker!!.setAnimation(animationSet)
        breatheMarker!!.startAnimation()
        return breatheMarker_center
    }

    fun createMaker(end: Location): Marker? {
        return activity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(2f)
                .anchor(0.1f, 0.1f).icon(BitmapDescriptorFactory.fromResource(R.drawable.myself_all)))
    }


    fun createScreenMarker(): Marker? {
        var screenMarker = activity.mAmap.addMarker(MarkerOptions().title("确定选点").snippet(getString(R.string.finaly_point))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(getWindowWidth()!! / 2, getWindowHight()!! / 2)
        return screenMarker
    }

    fun setDriverRoute(startPoint: LatLonPoint, endPoint: LatLonPoint, passPointDatas: ArrayList<Location>) {

        navi.addAMapNaviListener(this)
        CoroutineScope(uiContext).launch {
            activity._mActivity?.showProgressDialog("正在规划路径中......")
        }
        Log.e("result", "开始导航路径规划")
        var sList: MutableList<NaviLatLng> = ArrayList()
        var eList: MutableList<NaviLatLng> = ArrayList()
        var mStartLatlng = converNaviLatLngPoint(startPoint)
        var mEndLatlng = converNaviLatLngPoint(endPoint)
        sList.add(mStartLatlng!!)
        eList.add(mEndLatlng!!)
        var wayPoint = ArrayList<NaviLatLng>()
        passPointDatas.forEach {
            wayPoint.add(NaviLatLng(it.latitude, it.longitude))
        }
        var strategy = DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION
        try {
            strategy = navi.strategyConvert(true, false, false, false, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        navi.calculateDriveRoute(sList, eList, wayPoint, strategy)
    }


    var caculateRouteListener: CalculateRouteListener? = null

    override fun onCalculateRouteSuccess(p0: AMapCalcRouteResult?) {
        super.onCalculateRouteSuccess(p0)
        if (caculateRouteListener != null) {
            caculateRouteListener?.CalculateCallBack(p0)
        }
    }

}