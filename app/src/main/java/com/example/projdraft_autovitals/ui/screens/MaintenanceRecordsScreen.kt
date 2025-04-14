package com.example.projdraft_autovitals.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projdraft_autovitals.data.model.MaintenanceRecord
import com.example.projdraft_autovitals.ui.navigation.BottomNavigationMenu
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModel
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MaintenanceRecordsScreen(
    navController: NavController,
    sessionManager: SessionManager,
    carId: Int,
    viewModel: AutoVitalsViewModel
) {
    val context = LocalContext.current

    // Collects maintenance records from the ViewModel as state
    val records by viewModel.maintenanceRecords.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Tracks the currently selected maintenance record for editing
    var selectedRecord by remember { mutableStateOf<MaintenanceRecord?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var submittedQuery by remember { mutableStateOf("") }

    // Handles reminder dialog visibility and reminder data input
    var showReminderDialog by remember { mutableStateOf(false) }
    var reminderService by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var reminderNotes by remember { mutableStateOf("") }

    // Flags for triggering date/time pickers
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

    // Used to display validation errors for reminder fields
    var showReminderValidationError by remember { mutableStateOf(false) }

    // Tracks whether a reminder is set for the current record
    var isReminderSet by remember { mutableStateOf(false) }

    LaunchedEffect(carId) {
        viewModel.loadMaintenanceRecords(carId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF0D1B26)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
                .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Maintenance Records", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by Service Type", color = Color.White) },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            submittedQuery = searchQuery.text.trim()
                            searchQuery = TextFieldValue("")
                            keyboardController?.hide()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color.White,
                        focusedContainerColor = Color(0xFF1B2B3C),
                        unfocusedContainerColor = Color(0xFF1B2B3C)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        submittedQuery = searchQuery.text.trim()
                        searchQuery = TextFieldValue("")
                        keyboardController?.hide()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters records by submitted query (service type)
            val filteredRecords = if (submittedQuery.isBlank()) {
                records
            } else {
                records.filter {
                    it.service.contains(submittedQuery, ignoreCase = true)
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (filteredRecords.isEmpty()) {
                    Text("No records found.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    filteredRecords.forEach { record ->
                        MaintenanceItem(
                            record = record,
                            onEdit = {
                                selectedRecord = it
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteMaintenanceRecordById(it.id)
                                viewModel.loadMaintenanceRecords(carId)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedRecord = null
                    showDialog = true
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Record", color = Color.White, fontSize = 16.sp)
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

    // When editing or adding a record, show the dialog
    if (showDialog) {
        MaintenanceDialog(
            record = selectedRecord,
            carId = carId,
            onDismiss = { showDialog = false },

            // Saves the record with reminder statu
            onSave = { newRecord ->
                viewModel.insertMaintenanceRecord(
                    MaintenanceRecord(
                        id = newRecord.id,
                        service = newRecord.service,
                        serviceDate = newRecord.serviceDate,
                        notes = newRecord.notes,
                        carId = carId,
                        reminderSet = isReminderSet  // <- flag comes from reminder dialog
                    )
                )
                viewModel.loadMaintenanceRecords(carId)
                showDialog = false
                isReminderSet = false  // Reset after saving
            },

            // Opens the reminder dialog, pre-fills existing reminder if available
            onAddReminder = { service ->
                reminderService = service
                CoroutineScope(Dispatchers.IO).launch {
                    val existingReminder = viewModel.getReminderDirect(carId, service)
                    existingReminder?.let {
                        reminderDate = it.reminderDate
                        reminderTime = it.reminderTime
                        reminderNotes = it.notes ?: ""
                        isReminderSet = true
                    }
                    showReminderDialog = true
                }
            }
        )
    }

    if (showReminderDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d ->
                reminderDate = "$y-${"%02d".format(m + 1)}-${"%02d".format(d)}"
                showReminderDatePicker = false
                showReminderTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showReminderTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                reminderTime = "${"%02d".format(hour)}:${"%02d".format(minute)}"
                showReminderTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Handles reminder saving with basic validation
    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = {
                Text(
                    if (reminderDate.isNotBlank() && reminderTime.isNotBlank()) "Edit Reminder" else "Add Reminder",
                    color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = reminderDate,
                        onValueChange = {},
                        label = { Text("Reminder Date", color = Color.White) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showReminderDatePicker = true },
                        textStyle = TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedContainerColor = FieldContainerColor,
                            unfocusedContainerColor = FieldContainerColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = {},
                        label = { Text("Reminder Time", color = Color.White) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showReminderTimePicker = true },
                        textStyle = TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedContainerColor = FieldContainerColor,
                            unfocusedContainerColor = FieldContainerColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reminderNotes,
                        onValueChange = { reminderNotes = it },
                        label = { Text("Notes", color = Color.White) },
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedContainerColor = FieldContainerColor,
                            unfocusedContainerColor = FieldContainerColor
                        )
                    )
                    if (showReminderValidationError && reminderDate.isBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Please select a date", color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reminderDate.isBlank() || reminderTime.isBlank()) {
                            showReminderValidationError = true
                        } else {
                            viewModel.insertServiceReminder(
                                context = context,
                                carId = carId,
                                service = reminderService,
                                date = reminderDate,
                                time = reminderTime,
                                notes = reminderNotes
                            )
                            isReminderSet = true
                            showReminderDialog = false
                            showReminderValidationError = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        if (reminderDate.isNotBlank() && reminderTime.isNotBlank()) "Update" else "Save",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showReminderDialog = false },
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = DialogContainerColor,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun MaintenanceItem(record: MaintenanceRecord, onEdit: (MaintenanceRecord) -> Unit, onDelete: (MaintenanceRecord) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2B3C))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(record.service, color = Color.White, fontSize = 18.sp)
            Text("Date: ${record.serviceDate}", color = Color.Gray, fontSize = 14.sp)
            Text(
                record.notes ?: "No notes available",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp
            )
            if (record.reminderSet) {
                Text("Reminder Set", color = Color(0xFF4CAF50), fontSize = 12.sp)
            } else {
                Text("No Reminder Set", color = Color(0xFFEF5350), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onEdit(record) }, modifier = Modifier.testTag("editButton_${record.service}")) {
                    Text("Edit", color = Color(0xFF81D4FA))
                }
                TextButton(onClick = { onDelete(record) }, modifier = Modifier.testTag("deleteButton_${record.service}")) {
                    Text("Delete", color = Color(0xFFEF5350))
                }
            }
        }
    }
}

private val DialogContainerColor = Color(0xFF1B2B3C)
private val FieldContainerColor = Color(0xFF1B2B3C)
private val AccentColor = Color(0xFF0288D1)

@Composable
fun MaintenanceDialog(
    record: MaintenanceRecord?,
    carId: Int,
    onDismiss: () -> Unit,
    onSave: (MaintenanceRecord) -> Unit,
    onAddReminder: (String) -> Unit
) {
    val context = LocalContext.current

    // Uses existing record data if editing, otherwise initializes empty fields
    var service by remember { mutableStateOf(TextFieldValue(record?.service ?: "")) }

    var date by remember { mutableStateOf(record?.serviceDate ?: "") }
    var notes by remember { mutableStateOf(TextFieldValue(record?.notes ?: "")) }
    var showValidationError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d ->
                date = "$y-${"%02d".format(m + 1)}-${"%02d".format(d)}"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (record == null) "Add Record" else "Edit Record", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = service,
                    onValueChange = {
                        service = it
                        showValidationError = false
                    },
                    label = { Text("Service", color = Color.White) },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedContainerColor = FieldContainerColor,
                        unfocusedContainerColor = FieldContainerColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Date (YYYY-MM-DD)", color = Color.White) },
                    placeholder = { Text("Select a date", color = Color.LightGray) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedContainerColor = FieldContainerColor,
                        unfocusedContainerColor = FieldContainerColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes", color = Color.White) },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedContainerColor = FieldContainerColor,
                        unfocusedContainerColor = FieldContainerColor
                    )
                )

                if (showValidationError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Service and Date are required.", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onAddReminder(service.text) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (record != null) "Edit Reminder" else "Add Reminder", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (service.text.isBlank() || date.isBlank()) {
                    showValidationError = true
                } else {
                    showValidationError = false
                    onSave(
                        MaintenanceRecord(
                            id = record?.id ?: 0,
                            service = service.text,
                            serviceDate = date,
                            notes = notes.text,
                            carId = carId,
                            reminderSet = record?.reminderSet ?: false
                        )
                    )
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text("Save", color = Color.White)
            }
        },

        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DialogContainerColor
    )
}
