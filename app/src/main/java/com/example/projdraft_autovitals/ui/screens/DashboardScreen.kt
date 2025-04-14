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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.data.model.Car
import com.example.projdraft_autovitals.data.model.MaintenanceRecord
import com.example.projdraft_autovitals.ui.navigation.BottomNavigationMenu
import com.example.projdraft_autovitals.ui.navigation.Screen
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModel
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModelFactory
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModel
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModelFactory
import com.example.projdraft_autovitals.util.SessionManager

@Composable
fun DashboardScreen(
    navController: NavController,
    repository: AutoVitalsRepository,
    sessionManager: SessionManager
) {
    val carViewModel: CarViewModel = viewModel(
        factory = CarViewModelFactory(repository, sessionManager)
    )

    val autoVitalsViewModel: AutoVitalsViewModel = viewModel(
        factory = AutoVitalsViewModelFactory(repository)
    )

    val cars by carViewModel.cars.collectAsState()
    val maintenanceRecords by autoVitalsViewModel.maintenanceRecords.collectAsState()

    var selectedCar by remember { mutableStateOf<Car?>(null) }
    var expanded by remember { mutableStateOf(false) }

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
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 60.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val car = selectedCar
                Text(
                    text = car?.let { "${it.make} ${it.model} (${it.year})" } ?: "No car selected",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (car != null) Color.White else Color.Gray
                )

                if (cars.isNotEmpty()) {
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text("Select Car", color = Color.White)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            cars.forEach { carItem ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${carItem.make} ${carItem.model} (${carItem.year})",
                                            color = Color.Black
                                        )
                                    },
                                    onClick = {
                                        selectedCar = carItem
                                        expanded = false
                                        autoVitalsViewModel.loadMaintenanceRecords(carItem.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cars.isEmpty()) {
                EmptyState(
                    message = "You don't have any cars added yet. Tap below to get started.",
                    onAction = { navController.navigate(Screen.CarManagement.route) },
                    extraContent = {
                        Button(
                            onClick = { navController.navigate(Screen.ServiceCenters.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Find Nearby Service Centers", color = Color.White, fontSize = 16.sp)
                        }
                    }
                )
            } else if (selectedCar == null) {
                EmptyState(
                    message = "Please select a car to begin tracking its maintenance history.",
                    onAction = { expanded = true },
                    extraContent = {
                        Button(
                            onClick = { navController.navigate(Screen.ServiceCenters.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Find Nearby Service Centers", color = Color.White, fontSize = 16.sp)
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.CarManagement.route) },
                        modifier = Modifier.weight(1f), // smaller weight
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                    ) {
                        Text("Car Details", color = Color.White)
                    }

                    Button(
                        onClick = {
                            selectedCar?.let {
                                navController.navigate("maintenance_records/${it.id}")
                            }
                        },
                        modifier = Modifier.weight(1.2f), // slightly longer
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                    ) {
                        Text("Maintenance Records", color = Color.White)
                    }
                }


                Spacer(modifier = Modifier.height(25.dp))
                FeatureCards(car = selectedCar)
                Spacer(modifier = Modifier.height(25.dp))
                StatsSection(records = maintenanceRecords)
                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = { navController.navigate(Screen.ServiceCenters.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Find Nearby Service Centers", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        BottomNavigationMenu(
            navController = navController,
            sessionManager = sessionManager,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun EmptyState(
    message: String,
    onAction: () -> Unit,
    extraContent: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🚗 Welcome to AutoVitals!",
            fontSize = 22.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAction,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
        ) {
            Text("Select Car", color = Color.White)
        }

        extraContent?.let {
            Spacer(modifier = Modifier.height(16.dp))
            it()
        }
    }
}

@Composable
fun FeatureCards(car: Car?) {
    if (car == null) {
        Text("No car selected", color = Color.Gray)
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B26)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            car.imageUri?.let { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text("${car.make} ${car.model}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Year: ${car.year}", color = Color.LightGray)
            Text("Mileage: ${car.mileage} km", color = Color.LightGray)
            Text("VIN: ${car.vin}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatsSection(records: List<MaintenanceRecord>) {
    val latestRecords = records.sortedByDescending { it.serviceDate }.take(3)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        latestRecords.forEach { record ->
            StatCard(
                title = record.service,
                value = record.serviceDate,
                subtext = record.notes ?: "",
                modifier = Modifier.weight(1f)
            )
        }

        repeat(3 - latestRecords.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, subtext: String, modifier: Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B26)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, color = Color.White)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            if (subtext.isNotEmpty()) {
                Text(subtext, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
