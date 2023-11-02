package com.diu.mlab.foodie.zone.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopProfile(
    var shopInfo: ShopInfo = ShopInfo(),
    var foodList: List<FoodItem> = emptyList()
): Parcelable
