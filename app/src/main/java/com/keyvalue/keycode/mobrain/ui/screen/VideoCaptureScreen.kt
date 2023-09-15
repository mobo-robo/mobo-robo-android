package com.keyvalue.keycode.mobrain.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.keyvalue.keycode.mobrain.R
import com.keyvalue.keycode.mobrain.camera.CameraHelper
import com.keyvalue.keycode.mobrain.createVideoCaptureUseCase
import io.socket.client.Socket
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

var recording: Recording? = null
val recordingStarted: MutableState<Boolean> = mutableStateOf(false)

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoCaptureScreen(cameraHelper: CameraHelper, navController: NavController, socket: Socket) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )


    val previewView: PreviewView = remember { PreviewView(context) }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val recordingStarted: MutableState<Boolean> = remember { mutableStateOf(false) }

    val audioEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(previewView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            videoCapture.value = context.createVideoCaptureUseCase(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector.value,
                previewView = previewView
            )
        }
    }
    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            ) {
                videoCapture.value?.let { it1 ->
                    if (!recordingStarted.value)
                        manageRecordingState(
                            navController, recordingStarted,
                            it1, context, cameraHelper, socket
                        )
                }

            }
        }
    }

}

fun manageRecordingState(
    navController: NavController,
    audioEnabled: MutableState<Boolean>,
    videoCapture: VideoCapture<Recorder>,
    context: Context,
    cameraHelper: CameraHelper,
    socket: Socket
) {
    val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
        File(it, context.getString(R.string.app_name)).apply { mkdirs() }
    }
    if (!recordingStarted.value) {
        audioEnabled.value = true
        videoCapture.let { videoCapture ->
            recordingStarted.value = true


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                recording =
                    startRecodring(cameraHelper, context, videoCapture, mediaDir, audioEnabled, navController, socket)
            }
        }
    } else {
        recordingStarted.value = false
        recording?.stop()
    }
}


@RequiresApi(Build.VERSION_CODES.P)
fun startRecodring(
    cameraHelper: CameraHelper,
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    mediaDir: File?,

    audioEnabled: MutableState<Boolean>, navController: NavController, socket: Socket
): Recording? {
    val recording = cameraHelper.startRecordingVideo(
        context = context,
        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
        videoCapture = videoCapture,
        outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.cacheDir,
        executor = context.mainExecutor,
        audioEnabled = audioEnabled.value
    ) { event ->
        if (event is VideoRecordEvent.Finalize) {
            val uri = event.outputResults.outputUri
            if (uri != Uri.EMPTY) {
                val uriEncoded = URLEncoder.encode(
                    uri.toString(),
                    StandardCharsets.UTF_8.toString()
                )
                //navController.navigate("${Route.VIDEO_PREVIEW}/$uriEncoded")
                socket.emit("data", File(CameraHelper.path).readBytes())
                Log.d("POPE", "uri->" + uriEncoded)

                manageRecordingState(
                    navController,
                    audioEnabled,
                    videoCapture,
                    context,
                    cameraHelper,
                    socket
                )


            }
        } else if (event is VideoRecordEvent.Start) {
            Handler(Looper.getMainLooper()).postDelayed({
                manageRecordingState(
                    navController,
                    audioEnabled,
                    videoCapture,
                    context,
                    cameraHelper,
                    socket
                )
            }, 1000)
        }
    }

    return recording
}