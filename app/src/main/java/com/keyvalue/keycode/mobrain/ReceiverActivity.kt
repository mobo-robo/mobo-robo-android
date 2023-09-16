package com.keyvalue.keycode.mobrain

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.gson.JsonParser
import com.keyvalue.keycode.mobrain.Route.VIDEO_PREVIEW_ARG
import com.keyvalue.keycode.mobrain.aurdino.ACTION_USB_PERMISSION
import com.keyvalue.keycode.mobrain.aurdino.AurdinoHelpers
import com.keyvalue.keycode.mobrain.camera.CameraHelper
import com.keyvalue.keycode.mobrain.ui.screen.VideoCaptureScreen
import com.keyvalue.keycode.mobrain.ui.theme.MoBrainTheme
import com.keyvalue.keycode.mobrain.util.PreferenceHelper
import io.socket.client.IO
import io.socket.client.Socket
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

var mserialPort: UsbSerialDevice? = null

class ReceiverActivity : ComponentActivity() {

    val cameraHelper: CameraHelper = CameraHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceIdHeader = listOf(PreferenceHelper.getSharedPreferenceString(this, PreferenceHelper.DEVICE_ID))
        val deviceSecret = listOf(PreferenceHelper.getSharedPreferenceString(this, PreferenceHelper.SECRET))
        val headerMap = mutableMapOf<String, List<String>>()
        headerMap.put("deviceid", deviceIdHeader)
        headerMap.put("secret", deviceSecret)
        val socket = IO.socket("http://192.168.4.245:3000", IO.Options.builder().setExtraHeaders(headerMap).build())
        try {
            socket.connect().on(Socket.EVENT_CONNECT) {
                android.util.Log.d("event recived", it.toString());

            };
            socket.on("onControl:${deviceIdHeader.get(0)}") { parameters ->
                Log.d("controlle recevied", parameters.toString());
               main("""${parameters.get(0).toString() }}""",applicationContext)
            };

        } catch (e: Exception) {
            Log.d("POPE", "EXC" + e)
        }

        setContent {
            MoBrainTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    setUpUSB(LocalContext.current)
                    RequestPermissions(cameraHelper, navController, socket)

                }


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

fun main(json:String,context: Context) {
var patFound = false

    val jsonString1 = """{"content":"{x=-0.01, y=-0.0}"}"""

    // Second string
    val jsonString2 = """{"content":"{x=-0.01, y=-0.0}event: camera"}"""

    // Extract x and y from the first string
    val pattern1 = Pattern.compile("""x=(-?\d+\.\d+), y=(-?\d+\.\d+)""")
    val matcher1 = pattern1.matcher(jsonString1)
    if (matcher1.find()) {
        patFound = true
        val x1 = matcher1.group(1).toDouble()
        val y1 = matcher1.group(2).toDouble()
        println("x1: $x1, y1: $y1")
        var string: String = "$x1,$y1"
        val pattern2 = Pattern.compile("""event: (\w+)""")
        val matcher2 = pattern2.matcher(jsonString2)
        if (matcher2.find()) {
            val event = matcher2.group(1)
            println("event: $event")
        }
    }
    if(!patFound)
    {
        val jsonString = """{"content":"light:true"}"""

        // Extract "light" and "true" from the "content" field
        val pattern = Pattern.compile("""content:"(.*?)":(true|false)""")
        val matcher = pattern.matcher(jsonString)
        if (matcher.find()) {
            val key = matcher.group(1)
            val value = matcher.group(2)
            println("$key: $value")
            if(key=="music")
            {                var toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)

                if(value=="true")
            // Adjust volume as needed
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 200)
            else
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 10)
// Adjust duration as needed

            }
            else
            {
                var cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                var cameraId:String? = null
                try {
                    val cameraList = cameraManager.cameraIdList
                    if (cameraList.isNotEmpty()) {
                      cameraId = cameraList[0] // Use the first camera by default
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }



                // Toggle the flashlight when a button is clicked

                   if(value=="true")
                       cameraId?.let { cameraManager.setTorchMode(it, true) }
                else
                       cameraId?.let { cameraManager.setTorchMode(it, false) }



            }
        }
    }

    // Extract event from the second string



    // Print the extracted values

}

object Route {
    const val VIDEO = "video"
    const val VIDEO_PREVIEW_FULL_ROUTE = "video_preview/{uri}"
    const val VIDEO_PREVIEW = "video_preview"
    const val VIDEO_PREVIEW_ARG = "uri"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(cameraHelper: CameraHelper, navController: NavHostController, socket: Socket) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            NavHost(
                navController = navController,
                startDestination = Route.VIDEO
            ) {
                composable(Route.VIDEO) {
                    VideoCaptureScreen(navController = navController, cameraHelper = cameraHelper, socket = socket)
                }

                composable(Route.VIDEO_PREVIEW_FULL_ROUTE) {
                    val uri = it.arguments?.getString(VIDEO_PREVIEW_ARG) ?: ""
                    VideoPreviewScreen(uri = uri)
                }
            }
        }
    }


}


@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )
    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture
    )

    return videoCapture
}

@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            mainExecutor
        )
    }
}

fun setUpUSB(context: Context) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager


    val usbDevices: HashMap<String, UsbDevice> = usbManager.getDeviceList()
    if (!usbDevices.isEmpty()) {
        for (entry in usbDevices) {
            val pi = PendingIntent.getBroadcast(
                context, 0,
                Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(entry.value, pi)
        }
    }

    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        //Broadcast Receiver to automatically start and stop the Serial connection.
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {
                val granted = intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
                val device = intent
                    .getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)!! as UsbDevice
                if (granted) {
                    var connection = usbManager.openDevice(device)
                    var serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection)
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600)
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8)
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1)
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE)
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
                            AurdinoHelpers.mSerialPort = serialPort

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN")
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL")
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED")
                }
            }
        }
    }
    context.registerReceiver(broadcastReceiver, IntentFilter(ACTION_USB_PERMISSION))
}

@Composable
fun VideoPreviewScreen(
    uri: String
) {
    val context = LocalContext.current

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    DisposableEffect(
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    StyledPlayerView(context).apply {
                        player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

