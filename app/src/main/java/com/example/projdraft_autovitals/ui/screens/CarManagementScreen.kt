package com.example.projdraft_autovitals.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.data.model.Car
import com.example.projdraft_autovitals.ui.navigation.BottomNavigationMenu
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModel
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModelFactory
import com.example.projdraft_autovitals.util.SessionManager
import com.example.projdraft_autovitals.util.saveImageToInternalStorage
import androidx.compose.ui.window.Dialog

@Composable
fun CarManagementScreen(
    navController: NavController,
    repository: AutoVitalsRepository,
    sessionManager: SessionManager
) {
    val viewModel: CarViewModel = viewModel(
        factory = CarViewModelFactory(repository, sessionManager)
    )

    val cars by viewModel.cars.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    // Load cars once when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.loadCars()
    }

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
                "Manage Your Cars",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 60.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add new car button
            Button(
                onClick = { selectedCar = null; showDialog = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                modifier = Modifier.fillMaxWidth().testTag("addCarButton")
            ) {
                Text("Add New Car", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display list of cars or message if none exist
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cars.isEmpty()) {
                    Text("No cars added yet.", fontSize = 16.sp, color = Color.Gray)
                } else {
                    cars.forEach { car ->
                        CarItem(
                            car = car,
                            onEdit = { selectedCar = it; showDialog = true },
                            onDelete = { viewModel.deleteCar(it.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
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

        // Car dialog for add/edit
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF1B2B3C)
                    ) {
                        CarDialog(
                            car = selectedCar,
                            onDismiss = { showDialog = false },
                            onSave = { newCar ->
                                if (selectedCar == null) {
                                    viewModel.addCar(newCar)
                                } else {
                                    viewModel.updateCar(newCar.copy(id = selectedCar!!.id))
                                }
                                showDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarItem(car: Car, onEdit: (Car) -> Unit, onDelete: (Car) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B26))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display car image if available
            car.imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = "Car Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text("${car.make} ${car.model} (${car.year})", color = Color.White, fontSize = 18.sp)
            Text("Mileage: ${car.mileage} km", color = Color.Gray)

            // Edit and Delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onEdit(car) }) { Text("Edit", color = Color(0xFF81D4FA)) }
                TextButton(onClick = { onDelete(car) }) { Text("Delete", color = Color.Red) }
            }
        }
    }
}

@Composable
fun CarDialog(
    car: Car?,
    onDismiss: () -> Unit,
    onSave: (Car) -> Unit
) {
    val context = LocalContext.current

    // Input states
    var make by remember { mutableStateOf(car?.make ?: "") }
    var model by remember { mutableStateOf(car?.model ?: "") }
    var year by remember { mutableStateOf(car?.year?.toString() ?: "") }
    var mileage by remember { mutableStateOf(car?.mileage?.toString() ?: "") }
    var vin by remember { mutableStateOf(car?.vin ?: "") }
    var imageUri by remember { mutableStateOf(car?.imageUri?.let { Uri.parse(it) }) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    var showError by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color.Gray,
        focusedBorderColor = Color.White,
        cursorColor = Color.White
    )

    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            val maxDialogWidth = maxWidth

            Surface(
                modifier = Modifier
                    .width(maxDialogWidth)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1B2B3C)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),

                ) {
                    Text(
                        text = if (car == null) "Add Car" else "Edit Car",
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    // Image picker
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
                    ) {
                        Text("Choose Car Image", color = Color.White)
                    }

                    imageUri?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = "Car Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (showError && imageUri == null) {
                        Text("Car image is required", color = Color.Red, fontSize = 12.sp)
                    }

                    // Form fields with validation
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = make,
                        onValueChange = { make = it },
                        label = { Text("Make", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        isError = showError && make.isBlank(),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError && make.isBlank()) {
                        Text("Make is required", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        isError = showError && model.isBlank(),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError && model.isBlank()) {
                        Text("Model is required", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showError && year.isBlank(),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError && year.isBlank()) {
                        Text("Year is required", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mileage,
                        onValueChange = { mileage = it },
                        label = { Text("Mileage", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showError && mileage.isBlank(),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError && mileage.isBlank()) {
                        Text("Mileage is required", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vin,
                        onValueChange = { vin = it },
                        label = { Text("VIN", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        isError = showError && vin.isBlank(),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showError && vin.isBlank()) {
                        Text("VIN is required", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save and cancel buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val allValid = make.isNotBlank() &&
                                        model.isNotBlank() &&
                                        year.isNotBlank() &&
                                        mileage.isNotBlank() &&
                                        vin.isNotBlank() &&
                                        imageUri != null

                                if (allValid) {
                                    showError = false

                                    val savedPath = if (car == null || imageUri.toString() != car.imageUri) {
                                        saveImageToInternalStorage(context, imageUri!!)
                                    } else {
                                        car.imageUri
                                    }

                                    onSave(
                                        Car(
                                            id = car?.id ?: 0,
                                            userId = car?.userId ?: 1,
                                            carName = "$make $model",
                                            make = make,
                                            model = model,
                                            year = year.toIntOrNull() ?: 0,
                                            mileage = mileage.toIntOrNull() ?: 0,
                                            vin = vin,
                                            imageUri = savedPath
                                        )
                                    )
                                } else {
                                    showError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


