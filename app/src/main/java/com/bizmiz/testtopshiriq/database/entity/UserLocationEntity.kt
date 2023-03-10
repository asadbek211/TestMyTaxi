package com.bizmiz.testtopshiriq.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val longitude: String,
    val latitude: String
) {
    constructor(longitude: String, latitude: String) : this(0, longitude, latitude)
}