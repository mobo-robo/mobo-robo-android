package com.keyvalue.keycode.mobrain.client

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import com.keyvalue.keycode.mobrain.R
import com.keyvalue.keycode.mobrain.ui.theme.blackButton
import com.keyvalue.keycode.mobrain.ui.theme.transparentBlack
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Returns the absolute value of the given number.
 * @param size Joystick size
 * @param dotSize Joystick Dot size
 * @param backgroundImage Joystick Image Drawable
 * @param dotImage Joystick Dot Image Drawable
 */


fun shakeItBaby(context: Context) {
    if (Build.VERSION.SDK_INT >= 26) {
        (context.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
            VibrationEffect.createOneShot(
                300,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        (context.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
    }
}


fun showMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun JoyStick(
    modifier: Modifier = Modifier,
    size: Dp = 170.dp,
    dotSize: Dp = 40.dp,
    backgroundImage: Int = R.drawable.joy,
    dotImage: Int = R.drawable.joy_stick_button,
    moved: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current;
    Box(
        modifier = modifier
            .size(size)
    ) {
        val maxRadius = with(LocalDensity.current) { (size / 2).toPx() }
        val centerX = with(LocalDensity.current) { ((size - dotSize) / 2).toPx() }
        val centerY = with(LocalDensity.current) { ((size - dotSize) / 2).toPx() }

        var offsetX by remember { mutableStateOf(centerX) }
        var offsetY by remember { mutableStateOf(centerY) }

        var radius by remember { mutableStateOf(0f) }
        var theta by remember { mutableStateOf(0f) }

        var positionX by remember { mutableStateOf(0f) }
        var positionY by remember { mutableStateOf(0f) }

//        Image(
//            painterResource(id = backgroundImage),
//            "JoyStickBackground",
//            modifier = Modifier.size(size),
//        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(transparentBlack, transparentBlack),
                        startX = 0f,
                        endX = 100f
                    ),
                    shape = CircleShape
                )
        ) {
            // Your content goes here, such as text or other composables
        }
//        Box(
//            modifier = Modifier
//                .offset {
//                    IntOffset(
//                        (positionX + centerX).roundToInt(),
//                        (positionY + centerY).roundToInt()
//                    )
//                }
//                .background(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(Color.Blue, Color.Blue),
//                        startX = 0f,
//                        endX = 100f
//                    ),
//                    shape = CircleShape
//                ).   size(dotSize*1.4f).
//        )
        Image(
            painterResource(id = backgroundImage),
            "JoyStickBackground",
            modifier = Modifier
                .offset {
                    IntOffset(
                        (positionX + centerX).roundToInt(),
                        (positionY + centerY).roundToInt()
                    )
                }
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(blackButton, blackButton),
                        startX = 0f,
                        endX = 100f
                    ),
                    shape = CircleShape
                )
                .size(dotSize)
                .pointerInput(Unit) {

                    detectDragGestures(onDragEnd = {
                        offsetX = centerX
                        offsetY = centerY
                        radius = 0f
                        theta = 0f
                        positionX = 0f
                        positionY = 0f
                    }, onDragStart = {
                        shakeItBaby(context = context);
                    }) {


                            pointerInputChange: PointerInputChange, offset: Offset ->
                        val x = offsetX + offset.x - centerX
                        val y = offsetY + offset.y - centerY


                        pointerInputChange.consume()

                        theta = if (x >= 0 && y >= 0) {
                            atan(y / x)
                        } else if (x < 0 && y >= 0) {
                            (Math.PI).toFloat() + atan(y / x)
                        } else if (x < 0 && y < 0) {
                            -(Math.PI).toFloat() + atan(y / x)
                        } else {
                            atan(y / x)
                        }

                        radius = sqrt((x.pow(2)) + (y.pow(2)))

                        offsetX += offset.x
                        offsetY += offset.y

                        if (radius > maxRadius) {
                            polarToCartesian(maxRadius, theta)
                        } else {
                            polarToCartesian(radius, theta)
                        }.apply {
                            positionX = first
                            positionY = second
                        }

                    }
                }
                .onGloballyPositioned { coordinates ->
                    moved(
                        (coordinates.positionInParent().x - centerX) / maxRadius,
                        -(coordinates.positionInParent().y - centerY) / maxRadius
                    )
                },
        )
    }
}

private fun polarToCartesian(radius: Float, theta: Float): Pair<Float, Float> =
    Pair(radius * cos(theta), radius * sin(theta))