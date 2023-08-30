package com.naufal.belimotor.ui.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.AuthPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authPrefs: AuthPrefs,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    init {
        getLoginState()
    }

    private fun getLoginState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (authPrefs.getLoginState()) {
                _loginState.emit(LoginState(isLogged = true))
            } else {
                _loginState.emit(LoginState(isLogged = false))
            }
        }
    }

    data class LoginState(
        val isLogged: Boolean? = null
    )
}