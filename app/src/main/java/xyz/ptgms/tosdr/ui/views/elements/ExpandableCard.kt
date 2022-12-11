package xyz.ptgms.tosdr.ui.views.elements

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import xyz.ptgms.tosdr.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    title: String,
    titleFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    titleFontWeight: FontWeight = FontWeight.Bold,
    description: Any,
    descriptionFontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionMaxLines: Int = 10,
    shape: CornerBasedShape = Shapes().medium,
    padding: Dp = 12.dp
) {
    val expandedState = remember { mutableStateOf(false) }
    val rotationState = animateFloatAsState(
        targetValue = if (expandedState.value) 180f else 0f
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = TweenSpec(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = shape,
        onClick = {
            expandedState.value = !expandedState.value
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(6f),
                    text = title,
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .alpha(0.5f)
                        .weight(1f)
                        .rotate(rotationState.value),
                    onClick = {
                        expandedState.value = !expandedState.value
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.drop_down_arrow)
                    )
                }
            }
            if (expandedState.value) {
                Divider()
                if (description is String) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = description,
                        fontSize = descriptionFontSize,
                        fontWeight = descriptionFontWeight,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (description is TextWithSource) {
                    val context = LocalContext.current
                    Column {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = description.text,
                            fontSize = descriptionFontSize,
                            fontWeight = descriptionFontWeight,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                        Button(onClick = {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://tosdr.org/legal"))
                            startActivity(context, browserIntent, null)
                        }, modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()) {
                            Text(text = description.buttonText?: stringResource(id = R.string.visit_source))
                        }
                    }
                }
            }
        }
    }
}

data class TextWithSource(
    val text: String,
    val source: String,
    val buttonText: String?
)