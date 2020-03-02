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
    }


    var type = 0

    var value: Any? = null

    var secondValue: Any? = null

    var parameter: HashMap<Any, Any>? = null


}