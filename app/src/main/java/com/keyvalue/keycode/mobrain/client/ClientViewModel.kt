import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ClientViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()
    private val controllerSocketService: ControllerSocketService = ControllerSocketService();

    fun turnOnOffFlash() {
        _uiState.update { currentState ->
            currentState.copy(
                turnOnFlash = !currentState.turnOnFlash
            )
        }
        controllerSocketService.updateLightStatus(uiState.value.turnOnFlash);
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