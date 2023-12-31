import java.io.File

data class ClientUiState(
    val currentScrambledWord: String = "",
    val turnOnFlash: Boolean = false,
    val turnOnSound:Boolean=false,
    val turnOnMusic: Boolean=false,
    val turnOnLight:Boolean=false,
    val file: File?=null

)