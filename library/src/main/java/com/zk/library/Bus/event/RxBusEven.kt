package com.zk.library.Bus.event

class RxBusEven {

    companion object {
        var LocationReceive = 0x9999
        var WxLoginReLogin = 0x9998
        var DriverReturnRequest = 0x1001
        var PartyWebViewReturn = 0x1002
        var DriverMapPointRegeocodeSearched = 0x1003
        var MapCameraChangeFinish = 0x1004
        //组队相关
        var TeamSocketConnectSuccess = 0x1005
        var TeamSocketDisConnect = 0x1006
        var MinaDataReceive = 0x1007
        var Team_reject_even = 0x1008
        var BrowserSendTeamCode = 0x1011
        //骑行导航相关
        var DriverCancleByNavigation = 0x1009
        var DriverNavigationRouteChange = 0x1010
        val NAVIGATION_FINISH = 0x1011


        fun getInstance(type: Int): RxBusEven {
            var even = RxBusEven()
            even.type = type
            return even
        }

        fun getInstance(type: Int, value: Any): RxBusEven {
            var even = RxBusEven()
            even.type = type
            even.value = value
            return even
        }

    }

    var type = 0

    var value: Any? = null

    var secondValue: Any? = null

    var parameter: HashMap<Any, Any>? = null


}