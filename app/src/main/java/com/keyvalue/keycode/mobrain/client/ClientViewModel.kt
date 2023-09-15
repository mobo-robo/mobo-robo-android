import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ClientViewModel() : ViewModel() {
    private  var context: Context?=null;
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
       this.context=context;
        controllerSocketService = ControllerSocketService(context = context)
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