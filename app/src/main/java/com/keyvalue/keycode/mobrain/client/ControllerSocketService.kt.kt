import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

class ControllerSocketService {
    val socket = IO.socket("http://192.168.4.245:3000")

    fun sendControllerData(x: Double, y: Double) {
        val data = mapOf("x" to x, "y" to y)
        socket.emit("joystick", data);
    }

    init {
        socket.connect()
            .on(Socket.EVENT_CONNECT) {
                Log.d("event recived", it.toString());

            }
            .on("music") { parameters -> // do something on recieving a 'foo' event

            }
            .on("light") { parameters -> // do something on recieving a 'foo' event

            }.on("onStream") { parameters -> // do something on recieving a 'foo' event
                Log.d("STREAM DATA", "stream");
            }
        socket.emit("stream", "Test");
    }

    fun updateMusicStatus(status: Boolean) {
        socket.emit("music", status);
    }

    fun updateLightStatus(status: Boolean) {
        socket.emit("light", status);
    }


}