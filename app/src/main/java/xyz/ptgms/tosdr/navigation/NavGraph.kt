package xyz.ptgms.tosdr.navigation

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object About : Screen("about")
    object Settings : Screen("settings")
    object ServiceDetails : Screen("service/{serviceId}") {
        fun createRoute(serviceId: Int) = "service/$serviceId"
    }
}