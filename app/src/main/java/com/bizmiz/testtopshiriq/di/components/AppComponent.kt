package com.bizmiz.testtopshiriq.di.components

import com.bizmiz.testtopshiriq.di.modules.DatabaseModule
import com.bizmiz.testtopshiriq.ui.MainActivity
import com.bizmiz.testtopshiriq.ui.home.HomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface AppComponent {
    fun inject(homeFragment: HomeFragment)
}