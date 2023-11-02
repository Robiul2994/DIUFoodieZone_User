package com.diu.mlab.foodie.zone.domain.use_cases.order

import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import javax.inject.Inject

class PlaceOrder @Inject constructor (
    val repo: OrderRepo
) {
    operator fun invoke(
        orderInfo: OrderInfo, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(orderInfo.foodInfo.status == "Unavailable")
            failed.invoke("Food not available.")
        else if(orderInfo.userInfo.loc == "")
            failed.invoke("Must update your location.")
        else if(orderInfo.foodInfo.foodId.isNotEmpty())
            repo.placeOrder(orderInfo, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}