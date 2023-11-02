package com.diu.mlab.foodie.zone.domain.repo

import com.diu.mlab.foodie.zone.domain.model.FoodItem
import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.model.ShopProfile

interface UserMainRepo {
    fun getShopProfileList(success :(shopProfileList: List<ShopProfile>) -> Unit, failed :(msg : String) -> Unit)

    fun getShopProfile(shopEmail :String, success :(shopProfile: ShopProfile) -> Unit, failed :(msg : String) -> Unit)

    fun getFoodDetails(shopEmail :String,foodId: String, success :(food: FoodItem) -> Unit, failed :(msg : String) -> Unit)

    fun getUserProfile(email :String, success :(user: FoodieUser) -> Unit, failed :(msg : String) -> Unit)

    fun updateUserProfile(updatedUser: FoodieUser, picUpdated: Boolean, success :() -> Unit, failed :(msg : String) -> Unit)

}