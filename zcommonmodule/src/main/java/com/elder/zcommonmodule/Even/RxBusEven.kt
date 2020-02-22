package com.elder.zcommonmodule.Even


class RxBusEven {

    companion object {
        var DriverReturnRequest =  0x1001
        var PartyWebViewReturn = 0x1002
    }


    var type = 0

    var value: Any? = null

    var secondValue :Any ? = null

    var parameter: HashMap<Any, Any>? = null


}