package com.example.projdraft_autovitals.ui.navigation

// Defines the navigation routes used in the app.
// Each object represents a unique screen with a route string.
sealed class Screen(val route: String) {

    // Entry point of the app (splash/welcome screen)
    data object Welcome : Screen("welcome")

    // User login screen
    data object Login : Screen("login")

    // User registration screen
    data object Register : Screen("register")

    // Main dashboard after user login
    data object Dashboard : Screen("dashboard")

    // User profile screen (edit info, change password)
    data object Profile : Screen("profile")

    // Screen for viewing and managing user cars
    data object CarManagement : Screen("car_management")

    // Maintenance records screen, navigated with a carId parameter
    data object MaintenanceRecords : Screen("maintenance_records")

    // Google Maps view for nearby service centers
    data object ServiceCenters : Screen("service_centers")
}
