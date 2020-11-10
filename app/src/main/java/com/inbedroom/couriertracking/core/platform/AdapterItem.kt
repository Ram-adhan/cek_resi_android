package com.inbedroom.couriertracking.core.platform

sealed class AdapterItem<out T> {
    data class Item<out T>(val value: T?): AdapterItem<T>()

    class Header(
        val title: String
    ): AdapterItem<Nothing>()
}