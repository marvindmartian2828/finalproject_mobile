package com.example.projdraft_autovitals.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.ui.screens.*
import com.example.projdraft_autovitals.ui.viewmodel.*
import com.example.projdraft_autovitals.util.SessionManager

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: AutoVitalsRepository,
    sessionManager: SessionManager
) {
    // Shared LoginViewModel using factory with repository and context
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(repository, navController.context))

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {

        // Welcome screen with navigation to login or register
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onSignUpClick = { navController.navigate(Screen.Register.route) }
            )
        }

        // Login screen uses shared loginViewModel
        composable(Screen.Login.route) {
            LoginScreen(navController, loginViewModel)
        }

        // Registration screen
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        // Main dashboard with ViewModels and sessionManager
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, repository, sessionManager)
        }

        // Google Maps-based service center search
        composable(Screen.ServiceCenters.route) {
            ServiceCentersScreen(navController, sessionManager)
        }

        // Car CRUD screen
        composable(Screen.CarManagement.route) {
            CarManagementScreen(
                navController = navController,
                repository = repository,
                sessionManager = sessionManager
            )
        }

        // Maintenance records with carId parameter passed via route
        composable(
            "maintenance_records/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.IntType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: 0
            val viewModel: AutoVitalsViewModel = viewModel(factory = AutoVitalsViewModelFactory(repository))

            MaintenanceRecordsScreen(
                navController = navController,
                carId = carId,
                viewModel = viewModel,
                sessionManager = sessionManager
            )
        }

        // Profile screen with password and profile editing
        composable(Screen.Profile.route) {
            val profileViewModel: AutoVitalsViewModel = viewModel(factory = AutoVitalsViewModelFactory(repository))

            ProfileScreen(
                navController = navController,
                viewModel = profileViewModel,
                sessionManager = sessionManager
            )
        }
    }
}
