package com.inbedroom.couriertracking.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CekOngkirSetupValidation(
    var origin: SimpleLocation? = null,
    var destination: SimpleLocation? = null,
    var weight: Int = 0,
    var couriers: MutableList<String> = mutableListOf(),
    var formattedCourier: String = ""
) : Parcelable {

    fun isValid(): Boolean {
        if (origin != null && destination != null && weight > 0 && couriers.isNotEmpty()) {
            return true
        }

        return false
    }
}
