package com.naufal.belimotor.ui.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.AuthPrefs
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.common.addOnResultListener
import com.naufal.belimotor.data.motor.MotorRepository
import com.naufal.belimotor.data.motor.model.MotorDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val motorRepository: MotorRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    init {
        getMotorList()
    }

    private fun getMotorList() {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.getMotorList()
                .onStart {
                    _homeState.emit(HomeState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _homeState.emit(HomeState(motorList = response))
                        },
                        onFailure = { data, code, message ->
                            Log.i("HomeViewModel", message.toString())
                            _homeState.emit(HomeState(error = true, message = message))
                        },
                        onError = {
                            Log.i("HomeViewModel", it?.message.toString())
                            _homeState.emit(HomeState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    data class HomeState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val motorList: List<MotorDetail?>? = null,
    )
}