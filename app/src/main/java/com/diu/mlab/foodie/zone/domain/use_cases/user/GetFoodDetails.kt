package com.diu.mlab.foodie.zone.domain.use_cases.user

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class GetFoodDetails @Inject constructor (
    val repo: UserMainRepo
) {
    operator fun invoke(
        shopEmail: String,
        foodId: String,
        success: (food: FoodItem) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        if(shopEmail.isNotEmpty() && foodId.isNotEmpty())
            repo.getFoodDetails(shopEmail, foodId, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}