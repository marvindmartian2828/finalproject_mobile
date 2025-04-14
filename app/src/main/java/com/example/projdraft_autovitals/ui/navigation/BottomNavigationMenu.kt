package com.example.projdraft_autovitals.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.projdraft_autovitals.util.SessionManager

@Composable
fun BottomNavigationMenu(
    navController: NavController,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier
) {
    // Define bottom nav tabs with route and icon
    val tabs = listOf(
        Screen.Dashboard.route to Icons.Filled.Home,
        Screen.Profile.route to Icons.Filled.Person,
        "logout" to Icons.AutoMirrored.Filled.ExitToApp
    )

    // Get current screen route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF0D1B26),
        tonalElevation = 12.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF0D1B26))
            .background(Color(0xFF0D1B26))
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
    ) {
        tabs.forEach { (route, icon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (route == "logout") {
                        // Clear session and navigate to Welcome
                        sessionManager.clearSession()
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        // Navigate to selected route
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color(0xFF0288D1) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = route,
                                tint = if (isSelected) Color.White else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Label under icon
                        Text(
                            text = when (route) {
                                Screen.Dashboard.route -> "Dashboard"
                                Screen.Profile.route -> "Profile"
                                "logout" -> "Logout"
                                else -> "Unknown"
                            },
                            color = if (isSelected) Color(0xFF0288D1) else Color.White,
                            fontSize = 12.sp
                        )
                    }
                },

                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0288D1),
                    selectedTextColor = Color(0xFF0288D1),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}