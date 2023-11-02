package com.diu.mlab.foodie.zone.domain.repo

import com.diu.mlab.foodie.zone.domain.model.OrderInfo

interface OrderRepo {
    fun getOrderInfo(orderId: String, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit)

//    fun getCurrentOrderList(success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit)

    fun getMyOrderList(success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit)

    fun placeOrder(orderInfo: OrderInfo, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit)

    fun updateOrderInfo(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        shopEmail: String,
        runnerEmail: String,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    )


    fun updatePaymentType(
        orderId: String,
        type: String,
        shopEmail: String,
        success: () -> Unit,
        failed: (msg: String) -> Unit
    )
}