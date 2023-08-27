package com.naufal.belimotor.data.auth.model.response

import android.net.Uri

data class UserResponse(
    val userId: String = "",
    var image: String = "",
    val email: String = "",
    val name: String = "",
    val favMotor: String = "",
)
