package com.bizmiz.testtopshiriq.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bizmiz.testtopshiriq.database.dao.UserLocationDao
import com.bizmiz.testtopshiriq.database.entity.UserLocationEntity

@Database(entities = [UserLocationEntity::class], version = 1)
abstract class UserLocationDatabase : RoomDatabase() {

    abstract fun userLocationDao(): UserLocationDao
}