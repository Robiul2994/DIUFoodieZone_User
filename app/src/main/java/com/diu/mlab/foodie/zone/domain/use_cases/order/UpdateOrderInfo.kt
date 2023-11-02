package com.diu.mlab.foodie.zone.domain.use_cases.order

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class UpdateOrderInfo @Inject constructor (
    val repo: OrderRepo
) {
    operator fun invoke(
        orderId: String,
        varBoolName: String,
        value: Boolean,
        varTimeName: String,
        shopEmail: String,
        runnerEmail: String,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    ) {
        if(orderId.isNotEmpty())
            repo.updateOrderInfo(orderId, varBoolName, value, varTimeName, shopEmail, runnerEmail, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}