package com.naufal.belimotor.data.auth.model.request

data class RegisterRequest(
    val email: String = "",
    val password: String = "",
    val image: String = "",
    val name: String = "",
    val favMotor: String = "",
)
