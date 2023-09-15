import android.R.id
import android.content.Context
import android.util.Log
import com.keyvalue.keycode.mobrain.client.VideoCallback
import com.keyvalue.keycode.mobrain.util.PreferenceHelper
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ControllerSocketService(var context: Context,var callback: VideoCallback) {
    var socket: Socket? = null;

    fun sendControllerData(x: Double, y: Double) {
        val data = mapOf("x" to x, "y" to y)
        socket?.emit("joystick", data);
    }

    init {
        val deviceIdHeader = listOf(PreferenceHelper.getSharedPreferenceString(context, PreferenceHelper.DEVICE_ID))
        val deviceSecret = listOf(PreferenceHelper.getSharedPreferenceString(context, PreferenceHelper.SECRET))
        val headerMap = mutableMapOf<String, List<String>>()
        headerMap.put("deviceid", deviceIdHeader)
        headerMap.put("secret", deviceSecret)
        socket = IO.socket("http://192.168.4.245:3000", IO.Options.builder().setExtraHeaders(headerMap).build())
        val deviceId = PreferenceHelper.getSharedPreferenceString(context, PreferenceHelper.DEVICE_ID)
        Log.d("LISTNETING", "");
        socket?.connect()
            ?.on(Socket.EVENT_CONNECT) {
                Log.d("event recived", it.toString());

            }
            ?.on("music") { parameters -> // do something on recieving a 'foo' event

            }
            ?.on("light") { parameters -> // do something on recieving a 'foo' event

            }?.on("onStream:$deviceId") { parameters -> // do something on recieving a 'foo' event
                Log.d("STREAM DATA", "stream");
                try {
                    var data: ByteArray = (parameters.get(0) as JSONObject).get("content") as ByteArray
                     CoroutineScope(Dispatchers.IO).launch {                    byteArrayToFile(data);
                     }
//                    val input: InputStream = ByteArrayInputStream(data)
                } catch (e: Exception) {
                    Log.d("data parsing error", "")
                }

            }
        socket?.emit("stream", "Test");
    }

    suspend fun byteArrayToFile(videoByteArray: ByteArray) {
        val path = context.getExternalFilesDir(null)?.path + "file.mp4"
        var output: OutputStream;
        try {
            // Create a FileOutputStream to write the byte array to the file
            val input: InputStream = ByteArrayInputStream(videoByteArray)
            val output: OutputStream = FileOutputStream(path).also { output = it }
            val data = ByteArray(4096)
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
            }
            val file: File = File(path);
            callback.onVideoReceived(file)
            Log.d("Size", file.length().toString());
        } catch (e: IOException) {
            Log.d("file parsing error", "err")
            // Handle any potential IOExceptions here
        }
    }

    fun updateMusicStatus(status: Boolean) {
        socket?.emit("music", status);
    }

    fun updateLightStatus(status: Boolean) {
        socket?.emit("light", status);
    }


}