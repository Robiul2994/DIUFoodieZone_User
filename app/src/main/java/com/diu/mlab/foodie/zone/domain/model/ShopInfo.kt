package com.diu.mlab.foodie.zone.domain.model

import android.opengl.Visibility
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopInfo(
    val nm: String= "",
    val email: String= "",
    val phone: String= "",
    val paymentType: String = "Send Money",
    val pic: String= "",
    val cover: String= "",
    val qr: String= "",
    val loc: String= "",
    val visible: Boolean= false,
): Parcelable
