package com.diu.mlab.foodie.zone.domain.use_cases.order

import javax.inject.Inject

data class OrderUseCases @Inject constructor(
    val getOrderInfo: GetOrderInfo,
    val getMyOrderList: GetMyOrderList,
    val placeOrder: PlaceOrder,
    val updateOrderInfo: UpdateOrderInfo,
    val updatePaymentType: UpdatePaymentType
)
