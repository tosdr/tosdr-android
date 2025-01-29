package xyz.ptgms.tosdr.components.lists

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun getAdaptiveRoundedCornerShape(index: Int, lastIndex: Int): Shape {
    return RoundedCornerShape(
        topStart = if (index == 0) 24.dp else 8.dp,
        topEnd = if (index == 0) 24.dp else 8.dp,
        bottomStart = if (index == lastIndex) 24.dp else 8.dp,
        bottomEnd = if (index == lastIndex) 24.dp else 8.dp
    )
}