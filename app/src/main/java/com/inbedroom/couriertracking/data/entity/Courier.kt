package com.inbedroom.couriertracking.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Courier(
    val name: String,

    @SerializedName("img_url")
    val imgUrl: String,

    @SerializedName("codename")
    val code: String,

    val available: Boolean
): Parcelable