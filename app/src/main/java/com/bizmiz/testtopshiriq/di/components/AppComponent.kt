package com.bizmiz.testtopshiriq.di.components

import com.bizmiz.testtopshiriq.di.modules.DatabaseModule
import com.bizmiz.testtopshiriq.ui.MainActivity
import com.bizmiz.testtopshiriq.ui.home.HomeFragment
import com.bizmiz.testtopshiriq.ui.home.orders.OrdersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(ordersFragment: OrdersFragment)
}