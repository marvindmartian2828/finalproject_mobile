package com.example.projdraft_autovitals.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projdraft_autovitals.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreen(
                onLoginClick = { /* Navigate to Login Screen */ },
                onSignUpClick = { /* Navigate to SignUp Screen */ }
            )
        }
    }
}

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit, onSignUpClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Top section with background image and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.40f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.car_maintenance_bg2),
                contentDescription = "Car Maintenance Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
                            startY = 200f
                        )
                    )
            )

            // App name and subtitle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(50.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AutoVitals",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFAFA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    )

                    Text(
                        text = "Car Maintenance and Tracker App",
                        fontSize = 16.sp,
                        color = Color(0xFFF5F5F5).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }
            }
        }

        // Bottom section with intro text and buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .background(Color(0xFF121E28))
                .padding(50.dp)
        ) {

            // Tagline and description
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(25.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Stay Ahead of Repairs,\nDrive with Confidence.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFFAFA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Monitor your car's health, track services,\nand get notified before trouble hits.",
                        fontSize = 14.sp,
                        color = Color(0xFFF5F5F5).copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }

            // Login and Sign Up buttons
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f).padding(end = 8.dp).padding(bottom = 15.dp)
                    ) {
                        Text(text = "Log in", color = Color.Black, fontSize = 16.sp)
                    }

                    Button(
                        onClick = onSignUpClick,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(text = "Sign up", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onLoginClick = {}, onSignUpClick = {})
}
