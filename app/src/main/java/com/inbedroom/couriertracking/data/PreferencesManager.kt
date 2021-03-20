package com.inbedroom.couriertracking.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.CourierVersion
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        const val PREFERENCE_NAME = "App Preferences"
        const val CITY_LIST = "cities"
        const val COURIER_VERSION = "courier version"
        const val COURIER_VERSION_CODE = "courier version code"
        const val COURIER_LIST_FILE = "updated_couriers.json"
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

    fun readCourierAsset(fileName: String = "courier_list.json"): List<Courier> {

        val bufferReader = context.assets.open(fileName).bufferedReader()

        val jsonString = bufferReader.use {
            it.readText()
        }
        val gson = Gson()

        return gson.fromJson(jsonString, Array<Courier>::class.java).toList().filter {
            it.available
        }
    }

    fun getSavedVersion(): CourierVersion{
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

    fun saveCourierList(list: List<Courier>): Boolean{
        return try {
            val gson = Gson()
            val data = gson.toJson(list).toString()

            File(context.filesDir, COURIER_LIST_FILE).bufferedWriter().use { out ->
                out.write(data)
            }
            true
        }catch (e: IOException){
            false
        }
    }

    fun readCourierList(): List<Courier> {
        val jsonString: String
        return try {
            jsonString = File(context.filesDir, COURIER_LIST_FILE).bufferedReader().readLine()
            val gson = Gson()
            gson.fromJson(jsonString, Array<Courier>::class.java).toList().filter {
                it.available
            }
        } catch (e: IOException){
            readCourierAsset()
        }

    }
}