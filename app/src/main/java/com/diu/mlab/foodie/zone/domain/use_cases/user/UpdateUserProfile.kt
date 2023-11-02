package com.diu.mlab.foodie.zone.domain.use_cases.user

import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.UserMainRepo
import javax.inject.Inject

class UpdateUserProfile @Inject constructor (
    val repo: UserMainRepo
) {
    operator fun invoke(
        updatedUser: FoodieUser,
        picUpdated: Boolean,
        success :() -> Unit,
        failed :(msg : String) -> Unit
    ) {
        if(updatedUser.phone.isEmpty())
            failed.invoke("Phone number can't be empty.")
        else
            repo.updateUserProfile(updatedUser, picUpdated, success, failed)

    }

}