package com.naufal.belimotor.ui.features.motor_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MotorDetailViewModel @Inject constructor(
    private val motorRepository: MotorRepository
) : ViewModel() {

    private val _motorDetailState = MutableStateFlow(MotorDetailState())
    val motorDetailState = _motorDetailState.asStateFlow()

    private val _buyMotorState = MutableStateFlow(BuyMotorState())
    val buyMotorState = _buyMotorState.asStateFlow()

    fun getMotor(motorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.getMotor(motorId)
                .onStart {
                    _motorDetailState.emit(MotorDetailState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _motorDetailState.emit(MotorDetailState(motorDetail = response))
                        },
                        onFailure = { data, code, message ->
                            Log.i("MotorDetailViewModel", message.toString())
                            _motorDetailState.emit(
                                MotorDetailState(
                                    error = true,
                                    message = message
                                )
                            )
                        },
                        onError = {
                            Log.i("MotorDetailViewModel", it?.message.toString())
                            _motorDetailState.emit(
                                MotorDetailState(
                                    error = true,
                                    message = it?.message
                                )
                            )
                        }
                    )
                }
        }
    }

    fun buyMotor(motorDetail: MotorDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.buyMotor(motorDetail)
                .onStart {
                    _buyMotorState.emit(BuyMotorState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _buyMotorState.emit(
                                BuyMotorState(
                                    success = response,
                                    message = "Berhasil membeli motor, silahkan cek halaman transaksi"
                                )
                            )
                        },
                        onFailure = { data, code, message ->
                            Log.i("MotorDetailViewModel", message.toString())
                            _buyMotorState.emit(
                                BuyMotorState(
                                    error = true,
                                    message = message
                                )
                            )
                        },
                        onError = {
                            Log.i("MotorDetailViewModel", it?.message.toString())
                            _buyMotorState.emit(
                                BuyMotorState(
                                    error = true,
                                    message = it?.message
                                )
                            )
                        }
                    )
                }
        }
    }

    data class MotorDetailState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val motorDetail: MotorDetail? = null,
    )

    data class BuyMotorState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val success: Boolean? = null,
    )
}