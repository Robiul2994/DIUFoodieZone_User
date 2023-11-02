package com.diu.mlab.foodie.zone.domain.model

data class CartItem(
    val foodItem: FoodItem= FoodItem(),
    val quantity: Int=0,
    val type: String="",
    val typePrice: Int=0,
    val shopEmail: String = ""
    )
