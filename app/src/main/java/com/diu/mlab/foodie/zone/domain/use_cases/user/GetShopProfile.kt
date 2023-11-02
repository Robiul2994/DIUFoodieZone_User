package com.diu.mlab.foodie.zone.domain.use_cases.user

import com.diu.mlab.foodie.zone.domain.model.ShopProfile
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class GetShopProfile @Inject constructor (
    val repo: UserMainRepo
) {
    operator fun invoke(
        shopEmail :String,
        success :(shopProfile: ShopProfile) -> Unit,
        failed :(msg : String) -> Unit
    ){
        if(shopEmail.isNotEmpty() && shopEmail.contains("@"))
            repo.getShopProfile(shopEmail, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}