package com.example.projdraft_autovitals.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.projdraft_autovitals.BuildConfig
import com.example.projdraft_autovitals.ui.navigation.BottomNavigationMenu
import com.example.projdraft_autovitals.util.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import java.net.URL

private val SurfaceColor = Color(0xFF1B2B3C)
private val TextColorLight = Color(0xFFB0BEC5)

@Composable
fun ServiceCentersScreen(
    navController: NavController,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isPermissionGranted by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var searchResults by remember { mutableStateOf(listOf<Pair<String, LatLng>>()) }
    var selectedPlaceLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.0447, -114.0719), 10f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
        if (granted) {
            getUserLocation(fusedLocationClient, context) { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                userLocation = latLng
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            }
        } else {
            Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request location permission on first render
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            isPermissionGranted = true
            getUserLocation(fusedLocationClient, context) { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                userLocation = latLng
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            }
        }
    }

    // Animate map to selected location
    LaunchedEffect(selectedPlaceLocation) {
        selectedPlaceLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f),
                durationMs = 1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, SurfaceColor),
                        startY = 0f,
                        endY = 1000f
                    )
                )
                .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nearby Services", fontSize = 20.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search services...", color = TextColorLight) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor,
                        disabledContainerColor = SurfaceColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = TextColorLight,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (searchQuery.text.isNotBlank()) {
                            userLocation?.let { location ->
                                searchNearbyPlacesWithHttp(context, location, searchQuery.text.trim()) { results ->
                                    searchResults = results
                                    if (results.isEmpty()) {
                                        Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please enter a search keyword", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text("Search", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    getUserLocation(fusedLocationClient, context) {
                        val latLng = LatLng(it.latitude, it.longitude)
                        userLocation = latLng
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Location", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(265.dp)
                    .background(Color.DarkGray)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = isPermissionGranted),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    userLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Your Location",
                            snippet = "You are here"
                        )
                    }
                    searchResults.forEach { (name, latLng) ->
                        Marker(
                            state = MarkerState(position = latLng),
                            title = name,
                            snippet = "Tap to Navigate"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(265.dp)
                    .verticalScroll(rememberScrollState())
                    .background(SurfaceColor)
                    .padding(8.dp)
            ) {
                if (searchResults.isNotEmpty()) {
                    searchResults.forEach { (nameWithAddress, latLng) ->
                        val parts = nameWithAddress.split(" | ")
                        val name = parts.getOrNull(0) ?: "Unknown"
                        val address = parts.getOrNull(1) ?: "No address available"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlaceLocation = latLng }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = name, color = Color.White, fontSize = 16.sp)
                            Text(text = address, color = TextColorLight, fontSize = 13.sp)
                            HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                } else {
                    Text("No search results.", color = Color.Gray, fontSize = 14.sp)
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

// Attempts to get the user's last known location, or fetch current one if not available
@SuppressLint("MissingPermission")
fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { newLocation ->
                if (newLocation != null) {
                    onLocationReceived(newLocation)
                } else {
                    Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// Performs a nearby places search using HTTP request to Google Places API
fun searchNearbyPlacesWithHttp(
    context: Context,
    location: LatLng,
    keyword: String,
    onResults: (List<Pair<String, LatLng>>) -> Unit
) {
    val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
    val apiKey = BuildConfig.MAPS_API_KEY
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=${location.latitude},${location.longitude}" +
            "&radius=5000&keyword=$encodedKeyword&key=$apiKey"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = URL(url).readText()
            val json = JSONObject(response)
            val results = json.getJSONArray("results")

            val places = mutableListOf<Pair<String, LatLng>>()
            for (i in 0 until minOf(4, results.length())) {
                val item = results.getJSONObject(i)
                val name = item.getString("name")
                val address = item.optString("vicinity", "Address unavailable")
                val loc = item.getJSONObject("geometry").getJSONObject("location")
                val lat = loc.getDouble("lat")
                val lng = loc.getDouble("lng")
                places.add("$name | $address" to LatLng(lat, lng))
            }

            withContext(Dispatchers.Main) {
                onResults(places)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Search failed: ${e.message}", Toast.LENGTH_LONG).show()
                onResults(emptyList())
            }
        }
    }
}