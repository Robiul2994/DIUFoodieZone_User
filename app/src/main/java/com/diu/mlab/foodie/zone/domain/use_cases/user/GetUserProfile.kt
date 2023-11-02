package com.diu.mlab.foodie.zone.domain.use_cases.user

import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class GetUserProfile @Inject constructor (
    val repo: UserMainRepo
) {
    operator fun invoke(
        email :String,
        success :(user: FoodieUser) -> Unit,
        failed :(msg : String) -> Unit
    ) {
        if(email.isNotEmpty() && email.contains("@"))
            repo.getUserProfile(email, success, failed)
        else
            failed.invoke("Something went wrong")
    }

}