package xyz.ptgms.tosdr.tools.data

import androidx.compose.ui.graphics.Color

object GradeToHumanReadable {

    fun gradeToHuman(grade: String): String {
        return when (grade) {
            "A" -> "Good"
            "B" -> "Okay"
            "C" -> "Bad"
            "D" -> "Very Bad"
            "E" -> "Horrible"
            else -> "Unknown"
        }
    }

    fun gradeBackground(grade: String): Color {
        return when (grade) {
            "A" -> Color(0xFF46A546)
            "B" -> Color(0xFF61CF61)
            "B-" -> Color(0xFF70E670)
            "C" -> Color(0xFFF89406)
            "D" -> Color(0xFFC43C35)
            "E" -> Color(0xFF9E342E)
            else -> Color(0xFF999999)
        }
    }

    fun gradeForeground(grade: String): Color {
        return when (grade) {
            "A" -> Color(0xFFFFFFFF)
            "B" -> Color(0xFFFFFFFF)
            "C" -> Color(0xFF000000)
            "D" -> Color(0xFFFFFFFF)
            "E" -> Color(0xFFFFFFFF)
            else -> Color(0xFFFFFFFF)
        }
    }
}