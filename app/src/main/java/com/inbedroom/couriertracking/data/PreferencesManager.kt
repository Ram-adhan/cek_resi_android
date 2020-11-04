package com.inbedroom.couriertracking.data

import android.content.Context
import com.google.gson.Gson
import com.inbedroom.couriertracking.data.entity.CityEntity
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    context: Context
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
        editor.putString(CITY_LIST, string)
        editor.commit()
    }

    fun getSavedCityList(): List<CityEntity>{
        val json = sharedPreferences.getString(CITY_LIST, "")
        if (!json.isNullOrEmpty()){
            return Gson().fromJson(json, Array<CityEntity>::class.java).toList()
        }
        return mutableListOf()
    }
}