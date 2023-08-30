package com.naufal.belimotor.data.common

sealed class TransactionStatus(val status: String) {
    object Waiting : TransactionStatus("waiting")
    object Success : TransactionStatus("success")
    object Cancel : TransactionStatus("cancel")
}