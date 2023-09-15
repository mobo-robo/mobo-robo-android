package com.keyvalue.keycode.mobrain.login.model


import com.google.gson.annotations.SerializedName
import com.keyvalue.keycode.mobrain.login.model.Data

data class LoginResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("statusCode")
    val statusCode: Int
)