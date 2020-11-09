package com.inbedroom.couriertracking.data.room

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.CityEntity
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressDao: AddressDao,
    private val preferencesManager: PreferencesManager
): AddressRepository {
    override suspend fun addData(data: AddressEntity) {
        addressDao.insert(data)
    }

    override suspend fun addAllData(list: List<AddressEntity>) {
        list.forEach {
            addressDao.insert(it)
        }
    }

    override suspend fun removeAllData() {
        addressDao.delete()
    }

    override suspend fun getAllData(): List<AddressEntity> {
        return addressDao.getAddresses()
    }

    override fun saveCityList(list: List<CityEntity>) {
        preferencesManager.saveCityList(list)
    }

    override fun getCityList(): List<CityEntity> {
        return preferencesManager.getSavedCityList()
    }

}