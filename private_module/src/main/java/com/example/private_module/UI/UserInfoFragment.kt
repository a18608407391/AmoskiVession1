package com.example.private_module.UI

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.insertDriverInfo
import com.elder.zcommonmodule.DataBases.insertUserInfo
import com.elder.zcommonmodule.Entity.DriverInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.BR
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.Widget.CustomChart.AxisRenderer
import com.elder.zcommonmodule.Widget.CustomChart.LineSet
import com.example.private_module.Entitiy.PrivateEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.UserInfoViewModel
import com.example.private_module.databinding.FragmentUserBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_user.*
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils.PrivateModuleConfig.Companion.USER_INFO
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.getArray
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import org.cs.tec.library.http.NetworkUtil
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random


@Route(path = RouterUtils.FragmentPath.MYSELFPAGE)
class UserInfoFragment : BaseFragment<FragmentUserBinding, UserInfoViewModel>() {
    var userInfo: UserInfo? = null
    override fun initContentView(): Int {
        return R.layout.fragment_user
    }

    override fun initVariableId(): Int {
        return BR.user_fr_viewModel
    }

    var lableList = ArrayList<String>()
    fun getUserInfo(flag: Boolean) {
        if (userInfo == null) {
            Log.e("result", "用户数据JSON" + PreferenceUtils.getString(context, USER_INFO))
            userInfo = Gson().fromJson(PreferenceUtils.getString(context, USER_INFO), UserInfo::class.java)
        }
        viewModel?.fr_avatar?.set(getImageUrl(userInfo?.data?.headImgFile))
        viewModel?.name?.set(userInfo?.data?.name)
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
//                        POST /UserPersonalCenter/PersonalCenterDatil
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8")
                    .addHeader("appToken", token)
                    .post(body).url(Base_URL + "AmoskiActivity/UserPersonalCenter/PersonalCenterDetail").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(this!!.activity) {
            override fun onNext(it: String) {
                super.onNext(it)
                var data = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                var code = Gson().fromJson<PrivateEntity>(Gson().toJson(data.data), PrivateEntity::class.java)
                Log.e("result", "用户" + it)
                if (data.code == 0) {
                    //骑行数据
                    var max = code.queryUserDisCountRidingInfo?.ridingData!![0].maxDis
                    if (max.toInt() == 0) {
                        val cd = Calendar.getInstance()
                        if (cd.get(Calendar.MONTH) in 0..2) {
                            if (cd.get(Calendar.MONTH) == 0) {
                                vertical_text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                vertical_text3.text = (cd.get(Calendar.MONTH) + 1 - 1 + 12).toString() + "月"
                                vertical_text2.text = (cd.get(Calendar.MONTH) + 1 - 2 + 12).toString() + "月"
                                vertical_text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            } else if (cd.get(Calendar.MONTH) == 1) {
                                vertical_text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                vertical_text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                                vertical_text2.text = (cd.get(Calendar.MONTH) + 1 - 2 + 12).toString() + "月"
                                vertical_text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            } else if (cd.get(Calendar.MONTH) == 2) {
                                vertical_text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                vertical_text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                                vertical_text2.text = (cd.get(Calendar.MONTH) + 1 - 2).toString() + "月"
                                vertical_text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            }
                        } else {
                            vertical_text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                            vertical_text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                            vertical_text2.text = (cd.get(Calendar.MONTH) + 1 - 2).toString() + "月"
                            vertical_text1.text = (cd.get(Calendar.MONTH) + 1 - 3).toString() + "月"
                        }
                    } else {
                        code.queryUserDisCountRidingInfo!!.ridingData!!.forEachIndexed { index, progressData ->
                            if (index == 0) {
                                viewModel?.allTotal?.set(DecimalFormat("0").format(progressData.allTotalDis!! / 1000))
                                viewModel?.allTime?.set(ConvertUtils.formatTimeS(progressData.allTotalTime))
                            } else if (index == 1) {
                                vertical_progressbar4.progress = (progressData.allTotalDis * 100 / max).toInt()
                                vertical_text4.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 2) {
                                vertical_progressbar3.progress = (progressData.allTotalDis * 100 / max).toInt()
                                vertical_text3.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 3) {
                                vertical_progressbar2.progress = (progressData.allTotalDis * 100 / max).toInt()
                                vertical_text2.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 4) {
                                vertical_progressbar1.progress = (progressData.allTotalDis * 100 / max).toInt()
                                vertical_text1.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            }
                        }
                    }

                    viewModel?.dynamicsStr!!.set(code.PersonalCenterDatil?.DynamicCount.toString())
                    viewModel?.like!!.set(code.PersonalCenterDatil?.FabulousCount.toString())
                    viewModel?.fans!!.set(code.PersonalCenterDatil?.FansCount.toString())
                    viewModel?.focus!!.set(code.PersonalCenterDatil?.FollowCount.toString())
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)

                Log.e("result", "错误" + e.message)
            }
        })


//        Observable.create(ObservableOnSubscribe<Response> {
//            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
//            var map = HashMap<String, String>()
//            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
//            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryUserInfo").build()
//            var call = client.newCall(request)
//            var response = call.execute()
//            it.onNext(response)
//        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
//            return@Function it.body()?.string()
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(activity) {
//            override fun onNext(t: String) {
//                super.onNext(t)
////                Log.e("result", "用户接口" + t)
//                userInfo = Gson().fromJson<UserInfo>(t, UserInfo::class.java)
//                if (userInfo != null && userInfo?.code == 0) {
//                    PreferenceUtils.putString(context, USERID, userInfo?.data?.id)
//                    insertUserInfo(userInfo!!.data!!)

//
//                } else {
//                    if (userInfo?.code == 10009) {
//                        PreferenceUtils.putString(context, USER_TOKEN, null)
//                    }
//                    Toast.makeText(context, userInfo?.msg, Toast.LENGTH_SHORT).show()
//                    ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation()
//                    activty?.finish()
//                }
//            }
//
//            override fun onError(e: Throwable) {
//                super.onError(e)
//                Log.e("result", "用户" + e.message)
//            }
//
//            override fun onComplete() {
//                super.onComplete()
//            }
//        })
    }

    var twl: Array<String>? = null
    fun dismiss(d: ProgressDialog) {
        if (d != null && d.isShowing) {
            d.dismiss()
        }
    }

    override fun initData() {
        super.initData()
        setchart()
        viewModel?.inject(this)
    }

    fun setchart() {
//        line_chart.setYAxis(false)
//        line_chart.setXAxis(false)
//        line_chart.setBorderSpacing(ConvertUtils.dp2px(1F))
//        line_chart.setYLabels(AxisRenderer.LabelPosition.NONE)
//        line_chart.setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
//        line_chart.setFontSize(ConvertUtils.dp2px(8F))
//        line_chart.setLabelsColor(getColor(R.color.hint_color_edit))
//        line_chart.setOnEntryClickListener { setIndex, entryIndex, rect ->
//            line_chart.setIndex(entryIndex)
//        }
    }


    fun callback(info: UserInfo) {
        userInfo = info
        insertUserInfo(userInfo?.data!!)
        PreferenceUtils.putString(context, USER_INFO, Gson().toJson(userInfo))
//        \Activity\userHeaderPicoriginalImg\2019Y\12M\29D\8020191229161750219.jpg
//        \Activity\userHeaderPicoriginalImg\2019Y\12M\29D\8020191229164009587.jpg
        Log.e("result", "用户头像" + userInfo?.data?.headImgFile)
        CoroutineScope(uiContext).launch {
            viewModel?.fr_avatar?.set(userInfo?.data?.headImgFile)
            viewModel?.name?.set(userInfo?.data?.name)
        }
    }


//    private var mValues = floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.2f)
//    fun initChart(i: Int, lable: Array<String>?, half: Boolean) {
//        if (line_chart == null || lable == null) {
//            return
//        }
//        line_chart.reset()
//        line_chart.setIndex(i)
//        line_chart.notifyDataUpdate()
//
////        line_chart.setAxisLabelsSpacing(ConvertUtils.dp2px(20F))
////        line_chart.setBorderSpacing(ConvertUtils.dp2px(20F))
//        var data = LineSet()
//        if (half) {
//            var halfLable = arrayOf(lable!![lable.size - 6], lable!![lable.size - 5], lable!![lable.size - 4], lable!![lable.size - 3], lable!![lable.size - 2], lable!![lable.size - 1])
//            var halfValues = floatArrayOf(mValues[mValues.size - 6], mValues[mValues.size - 5], mValues[mValues.size - 4], mValues[mValues.size - 3], mValues[mValues.size - 2], mValues[mValues.size - 1])
//            halfLable.forEachIndexed { index, s ->
//                data.addPoint(s, halfValues[index])
//            }
//        } else {
//            lable!!.forEachIndexed { index, s ->
//                data.addPoint(s, mValues[index])
//            }
//        }
//        data.setColor(Color.parseColor("#62B297"))
////                .setGradientFill(intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#62B297")), null)
//                .setThickness(ConvertUtils.dp2px(3F).toFloat())
//                .setDotsRadius(ConvertUtils.dp2px(6F).toFloat())
//                .setDotsStrokeThickness(ConvertUtils.dp2px(3F).toFloat())
//                .setDotsStrokeColor(Color.WHITE)
//                .setDotsColor(Color.parseColor("#62B297"))
//                .beginAt(0)
//
//        var chartPaint = Paint()
//        chartPaint.color = getColor(R.color.hint_color_edit)
//        chartPaint.style = Paint.Style.STROKE
//        chartPaint.isAntiAlias = true
//        if (half) {
//            line_chart.setGrid(1, 5, chartPaint)
//        } else {
//            line_chart.setGrid(1, 11, chartPaint)
//        }
//        line_chart.addData(data)
//        line_chart.show()
//    }


}