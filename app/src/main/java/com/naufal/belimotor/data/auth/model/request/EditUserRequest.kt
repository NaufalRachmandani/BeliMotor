package com.naufal.belimotor.data.auth.model.request

import android.net.Uri

data class EditUserRequest(
    val image: Uri = Uri.EMPTY,
    val name: String = "",
    val favMotor: String = "",
)
