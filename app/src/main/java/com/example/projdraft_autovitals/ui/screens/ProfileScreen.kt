package com.example.projdraft_autovitals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projdraft_autovitals.ui.navigation.BottomNavigationMenu
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModel
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AutoVitalsViewModel,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val userId = sessionManager.getUserId()
    val user by viewModel.user.collectAsState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var triggerLogout by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    LaunchedEffect(triggerLogout) {
        if (triggerLogout) {
            delay(1000L)
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.loadUserById(userId)
        }
    }

    user?.let { currentUser ->
        var name by remember { mutableStateOf(TextFieldValue(currentUser.name)) }
        var email by remember { mutableStateOf(TextFieldValue(currentUser.email)) }
        var phone by remember { mutableStateOf(TextFieldValue(currentUser.phone)) }

        var nameError by remember { mutableStateOf(false) }
        var emailError by remember { mutableStateOf(false) }
        var phoneError by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Profile",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 60.dp, bottom = 30.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B26))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileTextField("Full Name", name, isEditing, { name = it }, nameError, "Full name is required")
                        ProfileTextField("Email", email, isEditing, { email = it }, emailError, "Enter a valid email address")
                        ProfileTextField("Phone", phone, isEditing, { phone = it }, phoneError, "Phone must be 10 digits")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            showErrors = true
                            nameError = name.text.isBlank()
                            emailError = !email.text.matches(emailPattern)
                            phoneError = phone.text.length != 10 || phone.text.any { !it.isDigit() }

                            if (!nameError && !emailError && !phoneError) {
                                viewModel.updateUser(
                                    currentUser.copy(
                                        name = name.text,
                                        email = email.text,
                                        phone = phone.text
                                    )
                                )
                                isEditing = false
                                showErrors = false
                            }
                        } else {
                            isEditing = true
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) Color(0xFF0288D1) else Color(0xFF4682B4)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag(if (isEditing) "saveProfileButton" else "editProfileButton")
                ) {
                    Text(if (isEditing) "Save Profile" else "Edit Profile", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showPasswordDialog = true },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Change Password", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            BottomNavigationMenu(
                navController = navController,
                sessionManager = sessionManager,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }

        if (showPasswordDialog) {
            ChangePasswordDialog(
                onDismiss = { showPasswordDialog = false },
                onSave = { newPassword ->
                    viewModel.updateUserPassword(currentUser.id, newPassword)
                    showPasswordDialog = false
                    sessionManager.clearSession()
                    Toast.makeText(context, "Password changed. Please log in again.", Toast.LENGTH_SHORT).show()
                    triggerLogout = true
                }
            )
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}


@Composable
fun ProfileTextField(
    label: String,
    value: TextFieldValue,
    isEditing: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.White) },
            enabled = isEditing,
            isError = isError,
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0288D1),
                unfocusedBorderColor = Color.DarkGray,
                cursorColor = Color.White,
                disabledBorderColor = Color.Gray,
                disabledTextColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .then(if (label == "Full Name") Modifier.testTag("nameField") else Modifier)
        )
        if (isError) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp)
        }
    }
}


@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color.White
                    )
                )
                if (errorMessage != null) {
                    Text(
                        errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Fields cannot be empty"
                        }
                        newPassword.length < 5 -> {
                            errorMessage = "Password must be at least 5 characters"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Passwords do not match"
                        }
                        else -> {
                            onSave(newPassword)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1B2B3C)
    )
}
