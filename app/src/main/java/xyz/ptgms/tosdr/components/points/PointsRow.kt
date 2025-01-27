package xyz.ptgms.tosdr.components.points

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.api.models.Point
import xyz.ptgms.tosdr.ui.theme.BadgeColors

@Composable
fun PointsRow(
    modifier: Modifier = Modifier,
    point: Point,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    original: Boolean = false
) {
    Surface(
        onClick = { onClick?.invoke() },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 58.dp),
        enabled = onClick != null,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(when(point.case.classification) {
                        "blocker" -> R.drawable.ic_rounded_block_24
                        "bad" -> R.drawable.ic_rounded_thumb_down_24
                        "good" -> R.drawable.ic_rounded_thumb_up_24
                        else -> R.drawable.ic_rounded_info_24
                    }),
                    contentDescription = null,
                    tint = when(point.case.classification) {
                        "blocker" -> BadgeColors.red
                        "bad" -> BadgeColors.orange
                        "good" -> BadgeColors.green
                        else -> BadgeColors.gray
                    }
                )

                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = if (!original && point.case.localized_title != null) {
                            point.case.localized_title
                        } else {
                            point.case.title
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        content?.invoke()
    }
}