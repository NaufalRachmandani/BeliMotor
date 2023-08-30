package com.naufal.belimotor.ui.features.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.belimotor.data.auth.AuthPrefs
import com.naufal.belimotor.data.auth.UserRepository
import com.naufal.belimotor.data.common.addOnResultListener
import com.naufal.belimotor.data.motor.MotorRepository
import com.naufal.belimotor.data.motor.model.MotorDetail
import com.naufal.belimotor.data.motor.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val motorRepository: MotorRepository
) : ViewModel() {

    private val _transactionState = MutableStateFlow(TransactionState())
    val transactionState = _transactionState.asStateFlow()

    init {
        getTransactionList()
    }

    private fun getTransactionList() {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.getTransactionList()
                .onStart {
                    _transactionState.emit(TransactionState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _transactionState.emit(TransactionState(transactionList = response))
                        },
                        onFailure = { data, code, message ->
                            Log.i("TransactionViewModel", message.toString())
                            _transactionState.emit(TransactionState(error = true, message = message))
                        },
                        onError = {
                            Log.i("TransactionViewModel", it?.message.toString())
                            _transactionState.emit(TransactionState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    data class TransactionState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val transactionList: List<Transaction?>? = null,
    )
}