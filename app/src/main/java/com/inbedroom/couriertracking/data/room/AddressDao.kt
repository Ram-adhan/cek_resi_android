package com.inbedroom.couriertracking.data.room

import androidx.room.*
import com.inbedroom.couriertracking.data.entity.AddressEntity

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AddressEntity)

    @Query("SELECT * FROM address")
    suspend fun getAddresses(): List<AddressEntity>

    @Query("DELETE FROM address")
    suspend fun delete()
}