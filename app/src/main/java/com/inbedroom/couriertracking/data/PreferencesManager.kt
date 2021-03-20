package com.inbedroom.couriertracking.data

import android.content.Context
import com.google.gson.Gson
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.CourierVersion
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val PREFERENCE_NAME = "App Preferences"
        const val CITY_LIST = "cities"
        const val COURIER_VERSION = "courier version"
        const val COURIER_VERSION_CODE = "courier version code"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun saveCityList(list: List<CityEntity>) {
        val string = Gson().toJson(list)
        editor.remove(CITY_LIST)
        editor.apply()
        editor.putString(CITY_LIST, string)
        editor.apply()
    }

    fun readCourierAsset(): List<Courier> {
        val fileName = "courier_list.json"

        val bufferReader = context.assets.open(fileName).bufferedReader()

        val jsonString = bufferReader.use {
            it.readText()
        }
        val gson = Gson()

        return gson.fromJson(jsonString, Array<Courier>::class.java).toList().filter {
            it.available
        }
    }

    fun getVersion(): CourierVersion{
        val verInt = sharedPreferences.getInt(COURIER_VERSION, 0)
        val verString = sharedPreferences.getString(COURIER_VERSION_CODE, "")
        return CourierVersion(verInt, verString?: "0")
    }

    fun saveLatestVersion(version: CourierVersion){
        editor.remove(COURIER_VERSION)
        editor.apply()
        editor.remove(COURIER_VERSION_CODE)
        editor.apply()
        editor.putInt(COURIER_VERSION, version.version)
        editor.apply()
        editor.putString(COURIER_VERSION_CODE, version.version_code)
    }
}