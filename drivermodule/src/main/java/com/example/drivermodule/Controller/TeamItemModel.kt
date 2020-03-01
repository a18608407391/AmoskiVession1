package com.example.drivermodule.Controller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.Entity.SoketBody.*
import com.elder.zcommonmodule.Inteface.Locationlistener
import com.elder.zcommonmodule.Widget.LoginUtils.BaseDialogFragment
import com.elder.zcommonmodule.Widget.LoginUtils.LoginDialogFragment
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.TeamStarting
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getImageUrl
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.zk.library.Bus.ServiceEven
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.MinaDataReceive
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketConnectSuccess
import com.zk.library.Bus.event.RxBusEven.Companion.TeamSocketDisConnect
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.android.synthetic.main.fragment_team.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TeamItemModel : ItemViewModel<MapFrViewModel>(), Locationlistener {
    //组队逻辑处理
    var markerList = HashMap<String, Marker>()
    var TeamInfo: TeamPersonInfo? = null
    var teamer: Int = 0
    var teamCode = ObservableField<String>()
    var teamId: String? = null
    var teamName: String? = null
    var markerListNumber = ArrayList<String>()
    var titleName = ObservableField<String>()
    var date = ObservableField<String>()
    var districtTv = ObservableField<String>()
    var visibleScroller = ObservableField<Boolean>(true)
    var teamNavigation = ObservableField<Boolean>(false)
    var create: CreateTeamInfoDto? = null
    var join: TeamPersonnelInfoDto? = null
    var isLoginTimeOut = false
    var startTime: Long = 0
    var location: AMapLocation? = null
    var type: String? = null   //队伍创建加入界面返回的参数
    var soketNavigation: SoketNavigation? = null
    fun restAllData() {
        markerList.clear()
        TeamInfo = null
        teamer = 0
        teamCode.set("")
        teamId = null
        teamName = null
        markerListNumber.clear()
        titleName.set("")
        date.set("")
        districtTv.set("")
        visibleScroller.set(true)
        teamNavigation.set(false)
        create = null
        join = null
        startTime = 0
        soketNavigation = null
    }

    lateinit var mapFr: MapFragment
    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            TeamSocketConnectSuccess -> {
                //组队Socket链接成功
                var so = Soket()
                so.type = 1000
                so.body = Soket.SocketRequest()
                so.body?.token = mapFr.token
                so.sendTime = System.currentTimeMillis()
                if (create != null) {
                    viewModel.TeamStatus?.teamCreate = create
                    so.teamCode = create?.teamCode
                    so.teamId = create?.id.toString()
                    startTime = create?.createTime!!
                } else if (join != null) {
                    viewModel.TeamStatus?.teamJoin = join
                    so.teamCode = join?.teamCode
                    so.teamId = join?.id.toString()
                    startTime = join?.createTime!!
                } else {
                    viewModel?.changerFragment(1)
                    if (viewModel.TeamStatus!!.teamJoin != null) {
                        join = viewModel.TeamStatus?.teamJoin
                        so.teamCode = viewModel.TeamStatus?.teamJoin?.teamCode
                        so.teamId = viewModel.TeamStatus?.teamJoin?.id.toString()
                        startTime = viewModel.TeamStatus?.teamJoin?.createTime!!
                    } else if (viewModel.TeamStatus?.teamCreate != null) {
                        create = viewModel.TeamStatus?.teamCreate
                        so.teamCode = viewModel.TeamStatus?.teamCreate?.teamCode
                        so.teamId = viewModel.TeamStatus?.teamCreate?.id.toString()
                        startTime = viewModel.TeamStatus?.teamCreate?.createTime!!
                    }
                }
                titleName.set("")
                so.userId = mapFr.user.data!!.id
                teamId = so.teamId
                teamCode.set(so.teamCode)
                sendOrder(so)
                viewModel.TeamStatus?.teamStatus = TeamStarting
                viewModel?.TeamStatus?.save()
            }
            TeamSocketDisConnect -> {
                //组队Socket已断开
            }
            MinaDataReceive -> {
                //组队数据接收
                doSocket((it.value) as BasePacketReceive)
            }
        }
    }

    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {
        viewModel?.listeners.add(this)
        mapFr = viewModel.mapActivity
        return super.ItemViewModel(viewModel)
    }


    fun sendOrder(n: Soket) {
        if (mapFr.onStart) {
            mapFr.showProgressDialog(getString(R.string.get_message))
        }
        var pos = ServiceEven()
        pos.type = "sendData"
        pos.gson = Gson().toJson(n) + "\\r\\n"
        RxBus.default?.post(pos)
    }


    var HeartTimeLimit = 0L

    private fun doSocket(it: BasePacketReceive) {
        if (mapFr.isAdded) {
            if (BaseApplication.isClose) {
                return
            }
            mapFr.dismissDialog()
            if (it.code == 0) {
                when (it.code) {
                    SocketDealType.HEART_BEAT.code -> {
                        //发送心跳
                        if (System.currentTimeMillis() - HeartTimeLimit < 10000) {
                            return
                        } else {
                            //10秒一次心跳
                            var so = Soket()
                            so.type = SocketDealType.HEART_BEAT.code
                            var pos = ServiceEven()
                            pos.type = "sendData"
                            pos.gson = Gson().toJson(so) + "\\r\\n"
                            RxBus.default?.post(pos)
                            HeartTimeLimit = System.currentTimeMillis()
                        }
                    }
                    SocketDealType.JOINTEAM.code -> {
                        //队员加入通知
                    }
                    SocketDealType.SENDPOINT.code -> {
                        //发送定位点
                    }
                    SocketDealType.OFFLINE.code -> {
                        //离线
                    }
                    SocketDealType.LEAVETEAM.code -> {
                        //离开队伍
                    }
                    SocketDealType.REJECTTEAM.code -> {
                        //队员被移除通知
                    }
                    SocketDealType.GETTEAMINFO.code -> {
                        //获取队内信息
                        if (mapFr.isAdded) {
                            TeamInfo = Gson().fromJson<TeamPersonInfo>(it.body, TeamPersonInfo::class.java)
                            if (!it.body!!.isEmpty()) {
                                initInfo()
                            }
                            if (TeamInfo?.redisData?.navigationPoint != null) {
                                if (TeamInfo?.redisData?.navigationPoint?.contains("\\")!!) {
                                    TeamInfo?.redisData?.navigationPoint = TeamInfo?.redisData?.navigationPoint?.replace("\\", "", true)
                                }
                                soketNavigation = Gson().fromJson<SoketNavigation>(TeamInfo?.redisData?.navigationPoint, SoketNavigation::class.java)
                                if (teamer.toString() == mapFr.user.data?.id) {
                                    teamNavigation.set(false)
                                    districtTv.set("")
                                } else {
                                    teamNavigation.set(true)
                                    districtTv.set(soketNavigation!!.navigation_end?.aoiName)
                                }
                            } else {
                                if (viewModel?.status?.navigationType == 1 && teamer.toString() == mapFr.user.data?.id) {
                                    sendNavigationNotify()
                                }
                                districtTv.set("")
                                teamNavigation.set(false)
                            }
                        }
                    }
                    SocketDealType.TEAMER_PASS.code -> {

                    }
                    SocketDealType.UPDATETEAMINFO.code -> {
                        //队内信息更新
                    }
                    SocketDealType.DISMISSTEAM.code -> {
                        //解散队伍
                    }
                    SocketDealType.UPDATE_MANAGER.code -> {

                    }
                    SocketDealType.NAVIGATION_START.code -> {
                        // 导航通知
                    }
                }
            } else {
                if (it.code == 10009) {
                    //登录超时 重新登录后，加入组队
                    //关闭Mina
                    //登录
                    //检查用户信息
                    //重新进入组队
                    if (!isLoginTimeOut) {
                        closeMina()
                        //返回到骑行界面
                        viewModel?.selectTab(0)

                        showLoginDialogFragment(mapFr)
                    }
                }
            }
        }
    }

    private fun sendNavigationNotify() {

    }


    fun doCreate(data: Intent?) {
        if (data != null) {
            type = data.extras.getString("type")
            when (type) {
                "create" -> {
                    create = data.getSerializableExtra("data") as CreateTeamInfoDto?
                    startMinaService()
                }
                "join" -> {
                    join = data.getSerializableExtra("data") as TeamPersonnelInfoDto?
                    startMinaService()
                }
                "cancle" -> {

                }
            }
        } else {

        }
    }

    fun initInfo() {
        if (TeamInfo != null && TeamInfo?.redisData != null && TeamInfo?.redisData?.createTime != null) {
            if (TeamInfo?.redisData?.teamName != null) {
                titleName.set(TeamInfo?.redisData?.teamName + "(" + TeamInfo?.redisData?.dtoList?.size + ")")
            }
            teamer = TeamInfo?.redisData?.teamer!!
            CoroutineScope(uiContext).launch {
                var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var d = Date(TeamInfo?.redisData?.validEndTime!!)
                date.set("有效期至：" + simple.format(d))
                addChildView(TeamInfo?.redisData!!.dtoList)
            }
        } else {

        }
    }

    var personDatas = ObservableArrayList<TeamPersonnelInfoDto>().apply {
        this.add(TeamPersonnelInfoDto())
    }
    var shareDialog: AlertDialog? = null

    var BottomChildClick = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            var entity = personDatas[t]
            if (t == 0) {
                if (TeamInfo?.redisData?.dtoList?.size == TeamInfo?.redisData?.teamMaxCount) {
                    Toast.makeText(context, getString(R.string.max_member) + TeamInfo?.redisData?.teamMaxCount + "人", Toast.LENGTH_SHORT).show()
                } else {
                    if (shareDialog == null) {
                        shareDialog = DialogUtils.createShareDialog(mapFr.activity!!, "", "")
                    } else if (!shareDialog!!.isShowing) {
                        shareDialog!!.show()
                    }
                    shareDialog?.findViewById<TextView>(R.id.share_frend)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneSession)
                        }
                        shareDialog!!.dismiss()
                    }
                    shareDialog?.findViewById<TextView>(R.id.share_frendQ)?.setOnClickListener {
                        if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                            CoroutineScope(uiContext).launch {
                                Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            shareWx(SendMessageToWX.Req.WXSceneTimeline)
                        }
                        shareDialog!!.dismiss()
                    }
                }
            } else {
                if (markerListNumber.contains(entity.memberId.toString())) {
                    var maker = markerList.get(entity.memberId.toString())
                    mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(maker!!.position, 15F), 1000, object : AMap.CancelableCallback {
                        override fun onFinish() {
                        }

                        override fun onCancel() {
                        }
                    })
                } else if (mapFr.user?.data?.memberId == entity.memberId.toString()) {
                    if (mapFr.getDrverController().curPosition != null) {
                        mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mapFr.getDrverController().curPosition!!.latitude, mapFr.getDrverController()!!.curPosition!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                            override fun onFinish() {
                            }

                            override fun onCancel() {
                            }
                        })
                    }
                }
            }
        }
    })

    fun shareWx(type: Int) {
        if (TeamInfo == null && mapFr.activity!!.isFinishing) {
            return
        }
        CoroutineScope(ioContext).async {
            var file = Glide.with(mapFr.activity!!)
                    .load(getImageUrl(TeamInfo?.redisData?.dtoList!![0].memberHeaderUrl))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            var files = file.get()

            if (files != null) {
                var bitmap = BitmapFactory.decodeFile(files.path)
                var newBitmap = ConvertUtils.compressByQuality(bitmap, 32000, true)
                var wx = WXWebpageObject()
                wx.webpageUrl = "http://amoski.net/yomoy/index.html?platform=android&teamCode=" + teamCode.get()
                var msg = WXMediaMessage()
                msg.mediaObject = wx
                msg.title = getString(R.string.team_code) + teamCode.get()
                msg.description = getString(R.string.share_description_by_team)
                msg.thumbData = newBitmap
                var req = SendMessageToWX.Req()
                req.transaction = System.currentTimeMillis().toString() + "img"
                req.message = msg
                req.scene = type
                BaseApplication.getInstance().mWxApi.sendReq(req)
            } else {
                Toast.makeText(context, "图片获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addChildView(dtoList: MutableList<TeamPersonnelInfoDto>) {
        if (dtoList != null && dtoList.size != 0) {
            personDatas.clear()
            personDatas.add(TeamPersonnelInfoDto())
            if (markerListNumber.size > dtoList.size - 1) {
                //数量有误
                markerListNumber.forEach {
                    markerList[it]?.remove()
                }
                markerListNumber.clear()
                markerList.clear()
            }

            dtoList.forEach {
                //                createImageMarker(LatLng(driverModel?.status.driverStartPoint?.latitude!!, driverModel?.status.driverStartPoint?.longitude!!), getImageUrl(it.memberHeaderUrl))

                if (markerListNumber.contains(it.memberId.toString())) {
                    var name = ""
                    if (it.memberName.trim().isEmpty()) {
                        name = getString(R.string.secret_name)
                    } else {
                        name = it.memberName
                    }
                    var m: List<String>
                    if (it.lastPoint != null) {
                        m = it.lastPoint.split(",")
                    } else {
                        m = it.joinAddr.split(",")
                    }
                    var maker = markerList[it.memberId.toString()]!!
                    maker.position = LatLng(m[0].toDouble(), m[1].toDouble())
                    if (location == null) {
                        if (it.status == "0") {
                            maker.title = "[离线]" + name + "," + "0M"
                        } else {
                            maker.title = name + "," + "0M"
                        }
                    } else {
                        var distanceTv = fomatDistance(LatLng(location!!.latitude, location!!.longitude), maker.position)
                        if (it.status == "0") {
                            maker.title = "[离线]" + name + "," + distanceTv
                        } else {
                            maker.title = name + "," + distanceTv
                        }
                    }
                    maker.snippet = it.teamRoleName
                    markerList.set(it.memberId.toString(), maker)
//                    maker.showInfoWindow()
                } else {
                    if (it.memberId.toString() != mapFr.user.data?.id) {
                        createImageMarker(it)

                    } else {

                    }
                }
                personDatas.add(it)
            }
        }

    }

    private fun createImageMarker(it: TeamPersonnelInfoDto) {

        var m = it.joinAddr.split(",")
        var position = LatLng(m[0].toDouble(), m[1].toDouble())
        var url = getImageUrl(it.memberHeaderUrl)
//                LatLng(m[0].toDouble(), m[1].toDouble()), getImageUrl(it.memberHeaderUrl), it.memberId,it.memberName
        var inflater = mapFr.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.teamm_maker_layout, null)
        view.findViewById<RelativeLayout>(R.id.maker_root).layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        var img = view.findViewById<ImageView>(R.id.team_marker_img)
        var maker = mapFr.mAmap.addMarker(MarkerOptions().position(position))
        var name = ""
        if (it.memberName.trim().isEmpty()) {
            name = getString(R.string.secret_name) + ",0M"
        } else {
            name = it.memberName + ",0M"
        }
        if (it.status == "0") {
            view!!.findViewById<ImageView>(R.id.team_marker_upBg).visibility = View.VISIBLE
            maker!!.title = "[离线]" + name
        } else {
            view!!.findViewById<ImageView>(R.id.team_marker_upBg).visibility = View.GONE
            maker!!.title = name
        }
        maker?.snippet = it.teamRoleName
        markerListNumber.add(it.memberId.toString())
        markerList.put(it.memberId.toString(), maker!!)
        var corners = CircleCrop()
        var options = RequestOptions().transform(corners).error(R.drawable.team_first).override(ConvertUtils.dp2px(48F), ConvertUtils.dp2px(48F))
        var listener = CustomListener(img, position, view, it, maker)
        Glide.with(mapFr.activity!!).load(url).apply(options).into(listener)
    }

    private fun fomatDistance(position: LatLng, markerPosition: LatLng): String? {
        var s = AMapUtils.calculateLineDistance(position, markerPosition)
        var distanceTv = ""
        if (s > 1000) {
            distanceTv = DecimalFormat("0.0").format(s / 1000) + "KM"
        } else {
            distanceTv = DecimalFormat("0.0").format(s) + "M"
        }
        return distanceTv
    }

    fun onClick(view: View) {

    }

    fun onComponentFinish() {

    }

    fun custionView(maker: Marker?, view: View?) {

    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun endTeam(b: Boolean) {

    }

    fun backToDriver() {
        if (BaseApplication.isClose) {
            //不发送消息 清空所有数据
            restAllData()
        } else {
            //发送消息
        }
        viewModel.changerFragment(0)

    }

    fun backToRoad() {
    }

    override fun onLocation(location: AMapLocation) {
        if (BaseApplication.MinaConnected) {
            //发送定位
            this.location = location
            var n = Soket()
            n.teamCode = teamCode.get()
            n.teamId = teamId
            n.userId = mapFr.user.data?.memberId
            n.type = SocketDealType.SENDPOINT.code
            n.body = Soket.SocketRequest()
            n.body!!.longitude = location.longitude
            n.body!!.latitude = location.latitude
            n.body!!.token = mapFr.token
            var pos = ServiceEven()
            pos.type = "sendData"
            pos.gson = Gson().toJson(n) + "\\r\\n"
            RxBus.default?.post(pos)
        }
    }

    override fun onDismiss(fr: BaseDialogFragment) {

    }

    inner class CustomListener : SimpleTarget<Drawable> {

        var img: ImageView? = null
        var position: LatLng? = null
        var view: View? = null
        lateinit var it: TeamPersonnelInfoDto

        var maker: Marker? = null

        constructor(img: ImageView, position: LatLng, view: View, it: TeamPersonnelInfoDto, maker: Marker) {
            this.img = img
            this.position = position
            this.view = view
            this.it = it
            this.maker = maker
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            if (maker != null) {
                img!!.setImageDrawable(resource)
                maker?.setIcon(BitmapDescriptorFactory.fromView(view))
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            if (maker != null) {
                img!!.setImageResource(R.drawable.default_avatar)
                maker?.setIcon(BitmapDescriptorFactory.fromView(view))
            }
        }
    }
}