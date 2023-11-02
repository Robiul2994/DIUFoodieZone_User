package com.diu.mlab.foodie.zone.domain.use_cases.user

import javax.inject.Inject

data class UserMainUseCases @Inject constructor(
    val getShopProfileList: GetShopProfileList,
    val getShopProfile: GetShopProfile,
    val getUserProfile: GetUserProfile,
    val updateUserProfile: UpdateUserProfile,
    val getFoodDetails: GetFoodDetails
)
