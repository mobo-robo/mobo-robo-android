import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.keyvalue.keycode.mobrain.client.VideoCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class ClientViewModel() : ViewModel() {
    var videoState: MutableStateFlow<File?>? = MutableStateFlow(null)
    private var context: Context? = null;
    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()
    private lateinit var controllerSocketService: ControllerSocketService

    fun turnOnOffFlash() {
        _uiState.update { currentState ->
            currentState.copy(
                turnOnFlash = !currentState.turnOnFlash
            )
        }

        controllerSocketService.updateLightStatus(uiState.value.turnOnFlash);
    }

    fun setContext(context: Context) {
        this.context = context;
        controllerSocketService = ControllerSocketService(context = context, object : VideoCallback {
            override suspend fun onVideoReceived(file: File) {
                videoState?.emit(file)
            }

        })
    }

    fun turnOnOffLight() {
        _uiState.update { currentState ->
            currentState.copy(
                turnOnLight = !currentState.turnOnLight
            )
        }
    }

    fun turnOnOffSound() {
        _uiState.update { currentState ->
            currentState.copy(
                turnOnSound = !currentState.turnOnSound
            )
        }
        controllerSocketService.updateMusicStatus(uiState.value.turnOnSound);
    }

    fun turnOnOffMusic() {
        _uiState.update { currentState ->
            currentState.copy(
                turnOnMusic = !currentState.turnOnMusic
            )
        }
        controllerSocketService.updateMusicStatus(uiState.value.turnOnMusic);
    }
}