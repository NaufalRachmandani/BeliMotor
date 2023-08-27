package com.naufal.belimotor.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.auth.model.request.RegisterRequest
import com.naufal.belimotor.data.common.addOnResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.register(registerRequest)
                .onStart {
                    _registerState.emit(RegisterState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _registerState.emit(RegisterState(success = response))
                        },
                        onFailure = { data, code, message ->
                            Log.i("RegisterViewModel", message.toString())
                            _registerState.emit(RegisterState(error = true, message = message))
                        },
                        onError = {
                            Log.i("RegisterViewModel", it?.message.toString())
                            _registerState.emit(RegisterState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    data class RegisterState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val success: Boolean? = null,
        val message: String? = null,
    )
}