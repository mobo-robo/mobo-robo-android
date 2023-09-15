package com.keyvalue.keycode.mobrain.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.util.Consumer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

class CameraHelper {
companion object{
    var path = ""
}
    fun startRecordingVideo(
        context: Context,
        filenameFormat: String,
        videoCapture: VideoCapture<Recorder>,
        outputDirectory: File,
        executor: Executor,
        audioEnabled: Boolean,
        consumer: Consumer<VideoRecordEvent>
    ): Recording? {
        val videoFile = File(
            outputDirectory,
            "feed" + ".mp4"
        )
        path = videoFile.path
        val outputOptions = FileOutputOptions.Builder(videoFile).build()
        return videoCapture.output
            .prepareRecording(context, outputOptions)
            .apply { if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            }
                if (audioEnabled) withAudioEnabled() }
            .start(executor, consumer)
    }

}