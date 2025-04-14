package com.example.projdraft_autovitals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projdraft_autovitals.data.model.User
import com.example.projdraft_autovitals.data.model.AutoVitalsDatabase
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.ui.navigation.Screen
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModel
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {

    // ViewModel setup
    val context = LocalContext.current
    val database = AutoVitalsDatabase.getDatabase(context)
    val repository = AutoVitalsRepository(database.autoVitalsDao())
    val factory = AutoVitalsViewModelFactory(repository)
    val viewModel: AutoVitalsViewModel = viewModel(factory = factory)

    // Form input states
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var showPassword by remember { mutableStateOf(false) }

    var showErrors by remember { mutableStateOf(false) }

    // Email validation pattern
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    var isUsernameTaken by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF1B2B3C)
                    ),
                    startY = 0f,
                    endY = 1000f
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", fontSize = 28.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        // Reusable input field composable
        @Composable
        fun inputField(
            tag: String,
            label: String,
            value: TextFieldValue,
            onValueChange: (TextFieldValue) -> Unit,
            isError: Boolean,
            errorMessage: String = "",
            isPassword: Boolean = false,
            showPassword: Boolean = false,
            onTogglePassword: () -> Unit = {}
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(tag),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    isError = isError,
                    trailingIcon = {
                        if (isPassword) {
                            val icon =
                                if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = onTogglePassword) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle password",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color.DarkGray,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp)) // Reduced space
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }


        // Input fields with validation
        inputField("nameField", "Full Name", name, { name = it }, showErrors && name.text.isBlank(), "Full name is required")
        inputField(
            "usernameField", "Username", username, { username = it },
            showErrors && (username.text.length !in 5..20 || isUsernameTaken),
            if (isUsernameTaken) "Username already taken" else "Username must be 5–20 characters"
        )
        inputField(
            "emailField", "Email", email, { email = it },
            showErrors && !email.text.matches(emailPattern),
            "Enter a valid email address"
        )
        inputField("phoneField", "Phone Number", phone, { phone = it }, showErrors && (phone.text.length != 10 || phone.text.any { !it.isDigit() }), "Phone must be 10 digits")
        inputField(
            "passwordField", "Password", password,
            { password = it },
            showErrors && (password.text.isBlank() || password.text.length < 5),
            "Password must be at least 5 characters",
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )
        inputField(
            "confirmPasswordField", "Confirm Password", confirmPassword,
            { confirmPassword = it },
            showErrors && (confirmPassword.text.isBlank() || password.text != confirmPassword.text),
            "Passwords do not match",
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Register and Cancel buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        showErrors = true

                        val isEmailValid = email.text.matches(emailPattern)
                        val isPhoneValid = phone.text.length == 10 && phone.text.all { it.isDigit() }
                        val isPasswordValid = password.text.length >= 5
                        val isConfirmPasswordValid = confirmPassword.text == password.text

                        val isUsernameAvailable = viewModel.isUsernameAvailable(username.text)
                        isUsernameTaken = !isUsernameAvailable

                        val allValid = name.text.isNotBlank() &&
                                username.text.length in 5..20 &&
                                isEmailValid &&
                                isPhoneValid &&
                                isPasswordValid &&
                                isConfirmPasswordValid &&
                                isUsernameAvailable

                        if (allValid) {
                            val newUser = User(
                                id = 0,
                                name = name.text,
                                username = username.text,
                                email = email.text,
                                phone = phone.text,
                                passwordHash = password.text
                            )
                            viewModel.insertUser(newUser)
                            navController.popBackStack()
                            navController.navigate(Screen.Login.route)
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .testTag("registerButton")
            ) {
                Text("Sign Up", color = Color.White, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = { navController.navigate(Screen.Welcome.route) },
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val dummyNavController = rememberNavController()
    RegisterScreen(navController = dummyNavController)
}
