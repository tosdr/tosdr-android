package xyz.ptgms.tosdr.tools.data

import androidx.annotation.Keep
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector

object Locale {
    // A list of all supported languages for translation APIs
    val languages = mutableListOf("  ", "de", "pl", "nl")
    val languages_libre = mutableListOf("ar", "az", "zh", "cs", "da", "nl", "eo", "fi", "fr", "de",
        "el", "he", "hi", "hu", "id", "ga", "it", "ja", "ko", "fa", "pl", "pt", "ru", "sk", "es",
        "sv", "tr", "uk")
}

@Keep
data class TosDR(
    val name: String,
    val id: String,
    val icon: String,
    val grade: String,
    var points: MutableList<Point>,
    val reviewed: Boolean,
    val urls: List<String>,
)

@Keep
data class Point(
    var title: MutableState<String>,
    var tlDr: MutableState<String>,
    var description: MutableState<String>,
    val quote: String,
    val type: String,
    val links: String,
    var translated: Boolean = false
)

@Keep
data class SearchResult(
    val name: String,
    val icon: String?,
    val page: String,
    val grade: String
)

@Keep
data class NavigationItem(
    val name: String,
    val icon: ImageVector,
    val page: String
)