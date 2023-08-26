package com.naufal.belimotor.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.AuthPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authPrefs: AuthPrefs,
) : ViewModel() {
//    private val _isLoggedState = MutableStateFlow(IsLoggedState())
//    val isLoggedState = _isLoggedState.asStateFlow()
//
//    init {
//        checkLoggedState()
//    }
//
//    private fun checkLoggedState() {
//        viewModelScope.launch {
//            val loginState = authPrefs.getLoginState()
//            _isLoggedState.emit(IsLoggedState(isLogged = loginState))
//        }
//    }
//
//    fun changeLoggedState() {
//        viewModelScope.launch {
//            authPrefs.setLoginState(true)
//        }
//    }
//
//    data class IsLoggedState(
//        val isLogged: Boolean? = null,
//    )
}