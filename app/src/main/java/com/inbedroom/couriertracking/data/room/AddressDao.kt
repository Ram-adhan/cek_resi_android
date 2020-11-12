package com.inbedroom.couriertracking.data.room

import androidx.room.*
import com.inbedroom.couriertracking.data.entity.AddressEntity

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AddressEntity)

    @Query("SELECT * FROM address")
    suspend fun getAddresses(): List<AddressEntity>

    @Query("SELECT * FROM address WHERE isCity = 1")
    suspend fun getAllCity(): List<AddressEntity>

    @Query("SELECT * FROM address WHERE isCity = 0 AND cityId = :cityId")
    suspend fun getDistrictFromCity(cityId: String): List<AddressEntity>

    @Query("DELETE FROM address WHERE isCity = 1")
    suspend fun removeAllCity()


    @Query("DELETE FROM address")
    suspend fun delete()
}