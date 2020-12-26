package com.inbedroom.couriertracking.data.entity.rajaongkir

import com.google.gson.annotations.SerializedName

data class TrackResult(
    val delivered: Boolean,

    @SerializedName("summary")
    val summary: SummaryTrack,

    @SerializedName("manifest")
    val manifestData: List<ManifestData>
)