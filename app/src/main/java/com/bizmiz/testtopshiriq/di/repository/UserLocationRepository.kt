package com.bizmiz.testtopshiriq.di.repository

import com.bizmiz.testtopshiriq.database.dao.UserLocationDao
import com.bizmiz.testtopshiriq.database.entity.UserLocationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocationRepository @Inject constructor(
    private val userLocationDao: UserLocationDao
) {

    suspend fun addUsersLocation(userLocationEntity: UserLocationEntity) =
        userLocationDao.addUserLocation(userLocationEntity)

    suspend fun getUsersLocation() = userLocationDao.getUserLocation()
}