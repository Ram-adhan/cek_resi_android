package com.inbedroom.couriertracking.utils

sealed class AddressItem<out T> {
    data class SubOrigin<out T>(val value: T): AddressItem<T>()
    data class SubDestination<out T>(val value: T): AddressItem<T>()
    class Empty(val value: String = ""): AddressItem<Nothing>()
}