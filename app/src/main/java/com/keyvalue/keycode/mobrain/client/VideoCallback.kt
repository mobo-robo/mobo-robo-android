package com.keyvalue.keycode.mobrain.client

import java.io.File

interface VideoCallback {
    suspend fun onVideoReceived(file:File)
}