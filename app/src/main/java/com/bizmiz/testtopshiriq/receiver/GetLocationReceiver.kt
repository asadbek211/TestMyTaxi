package com.bizmiz.testtopshiriq.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.bizmiz.testtopshiriq.database.entity.UserLocationEntity
import com.bizmiz.testtopshiriq.di.repository.UserLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetLocationReceiver @Inject constructor(
    private val userLocationRepository: UserLocationRepository
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_CURRENT_LOCATION") {
            val location = intent.extras?.get("LOCATION") as Location
            getLocation.invoke(location)
            CoroutineScope(Dispatchers.IO).launch {
                userLocationRepository.addUsersLocation(
                    UserLocationEntity(
                        longitude = location.longitude.toString(),
                        latitude = location.latitude.toString()
                    )
                )
                Log.d("USER_LOC", "Lat:${location.latitude},Long:${location.longitude}")
            }
        }
    }

    private var getLocation: (latLong: Location) -> Unit = {}
    fun getLocationListener(getLocation: (latLong: Location) -> Unit) {
        this.getLocation = getLocation
    }
}