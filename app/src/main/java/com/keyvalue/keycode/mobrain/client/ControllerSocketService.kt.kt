import android.content.Context
import android.util.Log
import com.keyvalue.keycode.mobrain.util.PreferenceHelper
import io.socket.client.IO
import io.socket.client.Socket

class ControllerSocketService(context: Context) {
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
            }
        socket?.emit("stream", "Test");
    }

    fun updateMusicStatus(status: Boolean) {
        socket?.emit("music", status);
    }

    fun updateLightStatus(status: Boolean) {
        socket?.emit("light", status);
    }


}