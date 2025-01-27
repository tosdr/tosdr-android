package xyz.ptgms.tosdr.navigation

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object About : Screen("about")
    object Donate : Screen("donate")
    object Team : Screen("team")
    object Settings : Screen("settings")
    object ServiceDetails : Screen("service/{serviceId}") {
        fun createRoute(serviceId: Int) = "service/$serviceId"
    }
    object PointView : Screen("point/{pointId}") {
        fun createRoute(pointId: Int) = "point/$pointId"
    }
    object GradesExplained : Screen("about/grades")
    object PointsExplained : Screen("about/points")
    object ServicesExplained : Screen("about/services")
    object Libraries : Screen("about/libraries")
}