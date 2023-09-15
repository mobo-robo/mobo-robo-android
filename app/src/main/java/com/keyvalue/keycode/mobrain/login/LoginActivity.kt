package com.keyvalue.keycode.mobrain.login

import LoginViewModel
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.keyvalue.keycode.mobrain.client.ClientActivity
import com.keyvalue.keycode.mobrain.ui.theme.MoBrainTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keyvalue.keycode.mobrain.R
import com.keyvalue.keycode.mobrain.Route
import com.keyvalue.keycode.mobrain.VideoPreviewScreen
import com.keyvalue.keycode.mobrain.ui.screen.VideoCaptureScreen
import com.keyvalue.keycode.mobrain.ui.theme.orangeButton
import com.keyvalue.keycode.mobrain.util.PreferenceHelper

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoBrainTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        LoginScreen()
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF08D8F7), Color(0xFF31013F)),

                    )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .size(150.dp)

            )
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "LOGIN",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp)
            )


            TextField(

                value = password,

                onValueChange = { password = it },
                label = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),


                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),

                )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    PreferenceHelper.setSharedPreferenceString(context,PreferenceHelper.SECRET,password.text)
                    loginViewModel.login(password.text,context);

                    // Perform authentication logic here
                    val userEmail = email.text
                    val userPassword = password.text
                    // You can implement authentication logic here
                    // For simplicity, we'll just show a toast message
                    context.startActivity(Intent(context, SelectModeActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = orangeButton),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "LOGIN",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current;
    Button(onClick = {
        context.startActivity(Intent(context, ClientActivity::class.java))
    }) {
        Text("Login")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoBrainTheme {
        TextField(

            value = "sds",

            onValueChange = { },
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(0.dp)

                .border(0.dp, Color.Blue),
            shape = RoundedCornerShape(12.dp),// Border color changes when focused
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
        )
    }
}
