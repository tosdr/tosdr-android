package xyz.ptgms.tosdr.components.points

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.ptgms.tosdr.api.models.Point

@Composable
fun PointsGroup(
    modifier: Modifier = Modifier,
    title: String,
    points: List<Point>,
    onPointClick: (Point) -> Unit = {},
    original: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                points.forEach { point ->
                    PointsRow(
                        point = point,
                        onClick = { onPointClick(point) },
                        original = original
                    )
                }
            }
        }
    }
}