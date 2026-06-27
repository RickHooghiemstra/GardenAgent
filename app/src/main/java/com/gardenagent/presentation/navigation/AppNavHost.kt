package com.gardenagent.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gardenagent.presentation.camera.AddManualCameraScreen
import com.gardenagent.presentation.camera.CameraListScreen
import com.gardenagent.presentation.camera.CameraViewScreen
import com.gardenagent.presentation.dashboard.DashboardScreen
import com.gardenagent.presentation.gardens.CreateGardenScreen
import com.gardenagent.presentation.gardens.GardenListScreen
import com.gardenagent.presentation.identify.IdentifyPlantScreen
import com.gardenagent.presentation.journal.AddJournalEntryScreen
import com.gardenagent.presentation.journal.JournalScreen
import com.gardenagent.presentation.plants.AddPlantScreen
import com.gardenagent.presentation.plants.PlantDetailScreen
import com.gardenagent.presentation.plants.PlantListScreen
import com.gardenagent.presentation.sensors.SensorScreen
import com.gardenagent.presentation.settings.NotificationSettingsScreen
import com.gardenagent.presentation.settings.SettingsScreen
import com.gardenagent.presentation.weather.WeatherScreen

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route) {
            Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
        },
        BottomNavItem("Plants", Screen.PlantList.route) {
            Icon(Icons.Default.LocalFlorist, contentDescription = "Plants")
        },
        BottomNavItem("Journal", Screen.Journal.route) {
            Icon(Icons.Default.MenuBook, contentDescription = "Journal")
        },
        BottomNavItem("Sensors", Screen.Sensors.route) {
            Icon(Icons.Default.Sensors, contentDescription = "Sensors")
        },
        BottomNavItem("Cameras", Screen.CameraList.route) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Cameras")
        },
    )

    val topLevelRoutes = bottomNavItems.map { it.route }.toSet()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = currentDestination?.route in topLevelRoutes ||
                currentDestination?.route == Screen.Dashboard.route

            if (showBottomBar || currentDestination?.hierarchy?.any { it.route in topLevelRoutes } == true) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAdvice = { navController.navigate(Screen.Weather.route) },
                    onNavigateToWeather = { navController.navigate(Screen.Weather.route) },
                    onNavigateToJournalEntry = { navController.navigate(Screen.AddJournalEntry.createRoute()) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToGardens = { navController.navigate(Screen.GardenList.route) },
                )
            }
            composable(
                Screen.PlantList.route,
                arguments = listOf(navArgument("gardenId") { nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val gardenId = backStackEntry.arguments?.getString("gardenId")?.toLongOrNull()
                PlantListScreen(
                    gardenId = gardenId,
                    onPlantClick = { id -> navController.navigate(Screen.PlantDetail.createRoute(id)) },
                    onAddPlant = { navController.navigate(Screen.AddPlant.route) }
                )
            }
            composable(Screen.PlantDetail.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toLong() ?: return@composable
                PlantDetailScreen(
                    plantId = plantId,
                    onBack = { navController.popBackStack() },
                    onAddJournalEntry = { navController.navigate(Screen.AddJournalEntry.createRoute(plantId)) },
                    onIdentify = { navController.navigate(Screen.IdentifyPlant.route) }
                )
            }
            composable(Screen.AddPlant.route) {
                AddPlantScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.IdentifyPlant.route) {
                IdentifyPlantScreen(
                    onBack = { navController.popBackStack() },
                    onPlantAdded = { /* plant added, stay on screen */ },
                )
            }
            composable(Screen.Journal.route) {
                JournalScreen(
                    onAddEntry = { navController.navigate(Screen.AddJournalEntry.createRoute()) }
                )
            }
            composable(Screen.AddJournalEntry.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toLongOrNull()
                AddJournalEntryScreen(
                    plantId = plantId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Weather.route) {
                WeatherScreen()
            }
            composable(Screen.Sensors.route) {
                SensorScreen()
            }
            composable(Screen.CameraList.route) {
                CameraListScreen(
                    onCameraClick = { sn -> navController.navigate(Screen.CameraView.createRoute(sn)) },
                    onSettings = { navController.navigate(Screen.Settings.route) },
                    onAddManualCamera = { navController.navigate(Screen.AddManualCamera.route) },
                )
            }
            composable(Screen.CameraView.route) { backStackEntry ->
                val sn = backStackEntry.arguments?.getString("deviceSn") ?: return@composable
                CameraViewScreen(
                    deviceSn = sn,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddManualCamera.route) {
                AddManualCameraScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNotificationSettings = { navController.navigate(Screen.NotificationSettings.route) },
                    onGardenList = { navController.navigate(Screen.GardenList.route) },
                )
            }
            composable(Screen.GardenList.route) {
                GardenListScreen(
                    onBack = { navController.popBackStack() },
                    onCreateGarden = { navController.navigate(Screen.CreateGarden.route) },
                    onGardenClick = { garden -> navController.navigate(Screen.PlantList.createRoute(garden.id)) },
                )
            }
            composable(Screen.CreateGarden.route) {
                CreateGardenScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
