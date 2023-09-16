package com.keyvalue.keycode.mobrain.client

import ClientViewModel
import EmptyVideoPreview
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.keyvalue.keycode.mobrain.VideoPreviewScreen
import com.keyvalue.keycode.mobrain.client.ui.theme.MoBrainTheme
import com.keyvalue.keycode.mobrain.ui.theme.transparentBlack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContent {
            val clientViewModel: ClientViewModel = viewModel()
            clientViewModel.setContext(context = LocalContext.current)
            MoBrainTheme {
                // A surface container using the 'background' color from the theme
                ClientRoot(clientViewModel)
            }
        }
    }

    @Composable
    private fun ClientRoot(clientViewModel: ClientViewModel) {
        val gameUiState by clientViewModel.uiState.collectAsState()
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val context = LocalContext.current

            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            JoyStick("Android", context = LocalContext.current, clientViewModel = clientViewModel)
        }
    }
}


@Composable
fun JoyStick(name: String, modifier: Modifier = Modifier, context: Context, clientViewModel: ClientViewModel) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
    ) {
        ControllerView(context = context, clientViewModel = clientViewModel)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ControllerView(context: Context, clientViewModel: ClientViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        val uri = remember {
            mutableStateOf("")
        }
        LaunchedEffect(Unit) {

        }
//        if (clientViewModel.videoState != null && clientViewModel.videoState?.collectAsState() != null) EmptyVideoPreview(
//            uri = Uri.parse(clientViewModel.videoState?.collectAsState()?.value?.path).toString()
//        )


        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {

                JoyStick(
                    Modifier.padding(0.dp),
                    size = 150.dp,
                    dotSize = 60.dp,
                ) { x: Float, y: Float ->
                    clientViewModel.sendControllerState(x, y, "camera")
                    Log.wtf("JoyStick", "$x, $y")
                }
            }

            bottomButtonsContainer(clientViewModel = clientViewModel)

        }

    }
}

@Composable
private fun bottomButtonsContainer(clientViewModel: ClientViewModel) {
    Row(
        Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        soundActions(clientViewModel = clientViewModel)
        lighActions(clientViewModel = clientViewModel)


    }
}

@Composable
private fun soundActions(clientViewModel: ClientViewModel) {
    val state = clientViewModel.uiState.collectAsState();
    Row(
        Modifier
            .background(transparentBlack)
            .padding(10.dp)
    ) {
        actionButton(
            onTap = {
                clientViewModel.turnOnOffSound();
            },
            icon = com.keyvalue.keycode.mobrain.R.drawable.sound,
            isOn = state.value.turnOnSound
        )
        actionButton(
            onTap = {
                clientViewModel.turnOnOffMusic()
            },
            icon = com.keyvalue.keycode.mobrain.R.drawable.music,
            isOn = state.value.turnOnMusic
        )
    }
}

@Composable
private fun lighActions(clientViewModel: ClientViewModel) {
    val state = clientViewModel.uiState.collectAsState();
    Row(
        Modifier
            .background(transparentBlack)
            .padding(10.dp)
    ) {
        actionButton(
            onTap = {
                clientViewModel.turnOnOffFlash()
            },
            icon = com.keyvalue.keycode.mobrain.R.drawable.torch,
            isOn = state.value.turnOnFlash
        )
        actionButton(
            onTap = {
                clientViewModel.turnOnOffLight();
            },
            isOn = state.value.turnOnLight,
            icon = com.keyvalue.keycode.mobrain.R.drawable.flash
        )
    }
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
private fun actionButton(onTap: () -> Unit, icon: Int, isOn: Boolean) {

    Box(

        Modifier
            .padding(end = 5.dp)


            .clip(CircleShape)
            .height(50.dp)
            .width(50.dp)
            .padding(10.dp)
            .conditional(isOn) {
                background(Color.White)
            }

            .clickable {
                onTap();

            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Image(
            painterResource(icon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun JoyStickPreview() {
    MoBrainTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            ControllerView(LocalContext.current, clientViewModel = viewModel())
        }
    }
}