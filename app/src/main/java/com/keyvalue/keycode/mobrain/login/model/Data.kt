package com.keyvalue.keycode.mobrain.login.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("secret")
    val secret: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)