package com.diu.mlab.foodie.zone.domain.use_cases.user

import com.diu.mlab.foodie.zone.domain.model.ShopProfile
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class GetShopProfileList @Inject constructor (
    val repo: UserMainRepo
) {
    operator fun invoke(
        success :(shopProfileList: List<ShopProfile>) -> Unit,
        failed :(msg : String) -> Unit
    ){
        repo.getShopProfileList(success, failed)
    }

}