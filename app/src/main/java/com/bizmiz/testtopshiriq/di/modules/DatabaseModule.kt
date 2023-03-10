package com.bizmiz.testtopshiriq.di.modules

import android.content.Context
import androidx.room.Room
import com.bizmiz.testtopshiriq.database.dao.UserLocationDao
import com.bizmiz.testtopshiriq.database.database.UserLocationDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(var context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = context


    @Provides
    @Singleton
    fun provideUserLocationDatabase(): UserLocationDatabase {
        return Room.databaseBuilder(context, UserLocationDatabase::class.java, "user_location_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserLocationDao(userLocationDatabase: UserLocationDatabase): UserLocationDao =
        userLocationDatabase.userLocationDao()
}