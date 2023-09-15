package com.keyvalue.keycode.mobrain.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keyvalue.keycode.mobrain.R
import com.keyvalue.keycode.mobrain.ReceiverActivity
import com.keyvalue.keycode.mobrain.client.ClientActivity
import com.keyvalue.keycode.mobrain.login.ui.theme.MoBrainTheme
import com.keyvalue.keycode.mobrain.ui.theme.orangeButton

class SelectModeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoBrainTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    val gradientColors = listOf(Color(0xFFFD7E14), Color(0xFFEC4E20))
val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF08D8F7), Color(0xFF31013F)),

                    )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Logo Image at the top
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null, // Provide a description for accessibility
            modifier = Modifier
                .size(160.dp)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(200.dp))
        // Sender Button
        Button(
            onClick = {
                // Handle sender button click
                context.startActivity(Intent(context, ClientActivity::class.java))

            },
            colors = ButtonDefaults.buttonColors(containerColor = orangeButton),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "SENDER",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Receiver Button
        Button(
            onClick = {
                // Handle receiver button click
                context.startActivity(Intent(context, ReceiverActivity::class.java))

            },
            colors = ButtonDefaults.buttonColors(containerColor = orangeButton),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "RECEIVER",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MoBrainTheme {
        MyScreen()
    }
}