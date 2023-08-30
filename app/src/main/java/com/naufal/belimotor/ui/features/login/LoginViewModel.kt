package com.naufal.belimotor.ui.features.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.AuthPrefs
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.common.addOnResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authPrefs: AuthPrefs,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.login(email, password)
                .onStart {
                    _loginState.emit(LoginState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _loginState.emit(LoginState(success = response))
                            authPrefs.setLoginState(response ?: false)
                        },
                        onFailure = { data, code, message ->
                            Log.i("LoginViewModel", message.toString())
                            _loginState.emit(LoginState(error = true, message = message))
                        },
                        onError = {
                            Log.i("LoginViewModel", it?.message.toString())
                            _loginState.emit(LoginState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    data class LoginState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val success: Boolean? = null,
        val message: String? = null,
    )
}