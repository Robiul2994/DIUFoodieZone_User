package com.diu.mlab.foodie.zone.domain.use_cases.order

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.OrderInfo
import com.diu.mlab.foodie.zone.domain.repo.OrderRepo
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetMyOrderList @Inject constructor (
    val repo: OrderRepo,
    private val firebaseUser: FirebaseUser?
) {
    operator fun invoke(
        success :(orderInfoList: List<OrderInfo>) -> Unit, failed :(msg : String) -> Unit
    ) {
        if(firebaseUser != null)
            repo.getMyOrderList(success, failed)
        else
            failed.invoke("Something went wrong")
    }

}