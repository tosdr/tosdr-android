package xyz.ptgms.tosdr.ui.views.elements

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeBackground


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    title: String,
    titleFontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    titleFontWeight: FontWeight = FontWeight.Bold,
    description: String,
    quote: String,
    link: String,
    type: String? = null,
    translation: Boolean = true,
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
        Column {
            val context = LocalContext.current
            if (!translation)
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GetIcon(type?:"")
                    if ((type ?: "") != "") {
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                    Text(
                        modifier = Modifier.weight(6f),
                        text = title,
                        fontSize = titleFontSize,
                        fontWeight = titleFontWeight,
                        maxLines = 7,
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
                            contentDescription = stringResource(id = R.string.drop_down_arrow)
                        )
                    }
                }
                if (expandedState.value) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    if (description.isNotEmpty() && description != "Generated through the annotate view") {
                        Text(
                            modifier = Modifier,
                            text = description,
                            fontSize = descriptionFontSize,
                            fontWeight = descriptionFontWeight,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (quote.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = stringResource(R.string.quote_from_policies),
                            fontSize = descriptionFontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "\"$quote\"",
                            fontSize = descriptionFontSize,
                            fontWeight = descriptionFontWeight,
                            maxLines = descriptionMaxLines,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (link.isNotEmpty() && link.startsWith("http")) {
                        Button(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                startActivity(context, intent, null)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.visit_source))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GetIcon(type: String) {
    return when (type) {
        "good" -> Icon(painter = painterResource(id = R.drawable.good), "Good",
            tint = gradeBackground("A"), modifier = Modifier.size(36.dp))
        "neutral" -> Icon(painter = painterResource(id = R.drawable.neutral), "Neutral",
            tint = Color.LightGray, modifier = Modifier.size(36.dp))
        "bad" -> Icon(painter = painterResource(id = R.drawable.bad), "Bad",
            tint = gradeBackground("C"), modifier = Modifier.size(36.dp))
        "blocker" -> Icon(painter = painterResource(id = R.drawable.blocker), "Blocker",
            tint = gradeBackground("E"), modifier = Modifier.size(36.dp))
        else -> Spacer(modifier = Modifier.padding(0.dp))
    }
}