package com.bizmiz.testtopshiriq.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmiz.testtopshiriq.di.repository.UserLocationRepository
import com.bizmiz.testtopshiriq.util.UserLocationResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserLocationViewModel @Inject constructor(
    private val userLocationRepository: UserLocationRepository
) : ViewModel() {
    private val stateFlow = MutableStateFlow<UserLocationResource>(UserLocationResource.Loading)
    fun getUsersLocation() {
        viewModelScope.launch {
            stateFlow.value = UserLocationResource.Success(userLocationRepository.getUsersLocation())
        }
    }
    fun getStateFlow(): StateFlow<UserLocationResource> {
        return stateFlow
    }
}