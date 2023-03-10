package com.bizmiz.testtopshiriq.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.bizmiz.testtopshiriq.database.entity.UserLocationEntity

@Dao
interface UserLocationDao {

    @Insert(onConflict = REPLACE)
    suspend fun addUserLocation(userLocationEntity: UserLocationEntity)

    @Query("select * from UserLocationEntity")
    suspend fun getUserLocation(): List<UserLocationEntity>
}