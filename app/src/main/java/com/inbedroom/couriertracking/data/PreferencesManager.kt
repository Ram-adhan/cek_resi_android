package com.inbedroom.couriertracking.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.Courier
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val PREFERENCE_NAME = "App Preferences"
        const val CITY_LIST = "cities"
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

    fun getSavedCityList(): List<CityEntity>{
        val json = sharedPreferences.getString(CITY_LIST, "")
        if (!json.isNullOrEmpty()){
            return Gson().fromJson(json, Array<CityEntity>::class.java).toList()
        }
        return mutableListOf()
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
}