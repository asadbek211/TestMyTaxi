package com.bizmiz.testtopshiriq.app

import android.app.Application
import com.bizmiz.testtopshiriq.di.components.AppComponent
import com.bizmiz.testtopshiriq.di.components.DaggerAppComponent
import com.bizmiz.testtopshiriq.di.modules.DatabaseModule

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule(this))
            .build()
    }
}