package com.example.projdraft_autovitals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projdraft_autovitals.ui.navigation.Screen
import com.example.projdraft_autovitals.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val uiState = loginViewModel.uiState
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black, Color(0xFF1B2B3C)),
                    startY = 0f,
                    endY = 1000f
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 28.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        // Input field with label, error message, and optional password toggle
        @Composable
        fun inputField(
            label: String,
            value: String,
            onValueChange: (String) -> Unit,
            isError: Boolean,
            errorMessage: String?,
            isPassword: Boolean = false,
            showPassword: Boolean = false,
            onTogglePassword: () -> Unit = {}
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = Color.White) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        if (isPassword) {
                            val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = onTogglePassword) {
                                Icon(imageVector = icon, contentDescription = "Toggle password", tint = Color.White)
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color.DarkGray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        cursorColor = Color.White
                    )
                )
                if (isError && errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Username input
        inputField(
            label = "Username",
            value = uiState.username,
            onValueChange = loginViewModel::updateUsername,
            isError = uiState.usernameError != null,
            errorMessage = uiState.usernameError
        )

        // Password input with toggle
        inputField(
            label = "Password",
            value = uiState.password,
            onValueChange = loginViewModel::updatePassword,
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )

        // General login error message
        uiState.errorMessage?.let {
            Text(it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Login and Cancel buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    loginViewModel.validateAndLogin {
                        loginViewModel.resetCredentials()
                        navController.navigate(Screen.Dashboard.route)
                    }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(if (uiState.isLoading) "Logging in..." else "Login", color = Color.White, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = {
                    loginViewModel.resetCredentials()
                    navController.navigate(Screen.Welcome.route)
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", fontSize = 16.sp)
            }
        }
    }
}
