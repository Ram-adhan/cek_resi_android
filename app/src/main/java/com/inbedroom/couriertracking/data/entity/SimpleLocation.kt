package com.inbedroom.couriertracking.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleLocation(
    val name: String,

    val type: String,

    @SerializedName("original_id")
    val id: String
): Parcelable
