package com.gardenagent.presentation.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object PlantList : Screen("plants")
    data object PlantDetail : Screen("plants/{plantId}") {
        fun createRoute(plantId: Long) = "plants/$plantId"
    }
    data object AddPlant : Screen("plants/add")
    data object IdentifyPlant : Screen("plants/identify")
    data object Journal : Screen("journal")
    data object AddJournalEntry : Screen("journal/add?plantId={plantId}") {
        fun createRoute(plantId: Long? = null) =
            "journal/add${plantId?.let { "?plantId=$it" } ?: ""}"
    }
    data object Weather : Screen("weather")
    data object Sensors : Screen("sensors")
    data object CameraList : Screen("cameras")
    data object CameraView : Screen("cameras/{deviceSn}") {
        fun createRoute(sn: String) = "cameras/$sn"
    }
    data object AddManualCamera : Screen("cameras/add")
    data object Advice : Screen("advice")
    data object Settings : Screen("settings")
    data object GardenList : Screen("gardens")
    data object CreateGarden : Screen("gardens/create")
    data object NotificationSettings : Screen("settings/notifications")
}
