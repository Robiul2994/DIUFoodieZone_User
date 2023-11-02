package com.diu.mlab.foodie.zone.domain.use_cases.order

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class GetOrderInfo @Inject constructor (
    val repo: OrderRepo
) {
    operator fun invoke(
        orderId: String, success :(orderInfo: OrderInfo) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(orderId.isNotEmpty())
            repo.getOrderInfo(orderId, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}