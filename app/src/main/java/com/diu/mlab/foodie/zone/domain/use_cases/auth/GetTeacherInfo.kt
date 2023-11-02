package com.diu.mlab.foodie.zone.domain.use_cases.auth

import com.diu.mlab.foodie.zone.domain.model.FoodieUser
import com.diu.mlab.foodie.zone.domain.repo.AuthRepo
import javax.inject.Inject

class GetTeacherInfo @Inject constructor (
    val repo: AuthRepo
) {
    suspend operator fun invoke(link: String): FoodieUser?{
        return if(link.contains("faculty.daffodilvarsity.edu.bd")) repo.getTeacherInfo(link)
        else null
    }
}