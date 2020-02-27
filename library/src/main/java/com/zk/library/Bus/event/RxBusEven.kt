package com.zk.library.Bus.event

class RxBusEven {

    companion object {
        var DriverReturnRequest =  0x1001
        var PartyWebViewReturn = 0x1002
        var DriverMapPointRegeocodeSearched = 0x1003
        var MapCameraChangeFinish = 0x1004
    }




    var type = 0

    var value: Any? = null

    var secondValue :Any ? = null

    var parameter: HashMap<Any, Any>? = null


}