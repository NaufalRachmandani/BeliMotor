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

    private val _updateTransactionState = MutableStateFlow(UpdateTransactionState())
    val updateTransactionState = _updateTransactionState.asStateFlow()

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

    fun proceedTransaction(transactionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.proceedTransaction(transactionId)
                .onStart {
                    _updateTransactionState.emit(UpdateTransactionState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _updateTransactionState.emit(UpdateTransactionState(success = response, message = "Berhasil melanjutkan transaksi"))
                        },
                        onFailure = { data, code, message ->
                            Log.i("TransactionViewModel", message.toString())
                            _updateTransactionState.emit(UpdateTransactionState(error = true, message = message))
                        },
                        onError = {
                            Log.i("TransactionViewModel", it?.message.toString())
                            _updateTransactionState.emit(UpdateTransactionState(error = true, message = it?.message))
                        }
                    )
                }
        }
    }

    fun cancelTransaction(transactionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            motorRepository.cancelTransaction(transactionId)
                .onStart {
                    _updateTransactionState.emit(UpdateTransactionState(loading = true))
                }
                .collect { appResult ->
                    appResult.addOnResultListener(
                        onSuccess = { response ->
                            _updateTransactionState.emit(UpdateTransactionState(success = response, message = "Berhasil membatalkan transaksi"))
                        },
                        onFailure = { data, code, message ->
                            Log.i("TransactionViewModel", message.toString())
                            _updateTransactionState.emit(UpdateTransactionState(error = true, message = message))
                        },
                        onError = {
                            Log.i("TransactionViewModel", it?.message.toString())
                            _updateTransactionState.emit(UpdateTransactionState(error = true, message = it?.message))
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

    data class UpdateTransactionState(
        val loading: Boolean? = null,
        val error: Boolean? = null,
        val message: String? = null,
        val success: Boolean? = null,
    )
}