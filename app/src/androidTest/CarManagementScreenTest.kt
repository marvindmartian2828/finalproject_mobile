package com.example.projdraft_autovitals

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.projdraft_autovitals.data.model.Car
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarManagementScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeCars = MutableStateFlow<List<Car>>(emptyList())

    // ✅ Create a simplified fake ViewModel
    private val fakeViewModel = object : CarViewModel(null, null) {
        override val cars = fakeCars.asStateFlow()

        override fun addCar(car: Car) {
            fakeCars.value = fakeCars.value + car
        }

        override fun loadCars() {
            // No-op for test
        }
    }

    @Test
    fun addCar_shouldDisplayInList() {
        composeTestRule.setContent {
            com.example.projdraft_autovitals.ui.screens.CarManagementScreenTestWrapper(viewModel = fakeViewModel)
        }

        // Click "Add New Car" button using testTag
        composeTestRule.onNodeWithTag("addCarButton").performClick()

        // Fill in form fields using testTags
        composeTestRule.onNodeWithTag("makeField").performTextInput("Toyota")
        composeTestRule.onNodeWithTag("modelField").performTextInput("Camry")
        composeTestRule.onNodeWithTag("yearField").performTextInput("2022")
        composeTestRule.onNodeWithTag("mileageField").performTextInput("12000")
        composeTestRule.onNodeWithTag("vinField").performTextInput("VIN123456789")

        // We skip image input for now (since it's a file picker)

        // Click Save
        composeTestRule.onNodeWithTag("saveButton").performClick()

        // ✅ Check if car was added (by checking car make or model)
        composeTestRule.onNodeWithText("Toyota Camry (2022)").assertIsDisplayed()
    }
}

@Composable
fun CarManagementScreenTestWrapper(viewModel: CarViewModel) {
    val cars by viewModel.cars.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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

        Button(
            onClick = { selectedCar = null; showDialog = true },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth().testTag("addCarButton")
        ) {
            Text("Add New Car", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
    }

    if (showDialog) {
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
