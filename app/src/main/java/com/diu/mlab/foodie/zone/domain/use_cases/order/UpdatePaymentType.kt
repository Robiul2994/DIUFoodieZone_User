package com.diu.mlab.foodie.zone.domain.use_cases.order

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class UpdatePaymentType @Inject constructor (
    val repo: OrderRepo
) {
    operator fun invoke(
        orderId: String,
        type: String,
        shopEmail: String,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    ) {
        if(orderId.isNotEmpty())
            repo.updatePaymentType(orderId, type, shopEmail, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}