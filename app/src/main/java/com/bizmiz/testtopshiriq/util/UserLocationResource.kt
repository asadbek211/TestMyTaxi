package com.bizmiz.testtopshiriq.util

sealed class UserLocationResource {

    object Loading : UserLocationResource()

    data class Success(val response: Any) : UserLocationResource()

    data class Error(val message: String) : UserLocationResource()
}