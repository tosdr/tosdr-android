package xyz.ptgms.tosdr.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.points.PointsGroup
import xyz.ptgms.tosdr.navigation.Screen
import xyz.ptgms.tosdr.ui.theme.BadgeColors
import xyz.ptgms.tosdr.ui.theme.ToSDRColorScheme
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(serviceId: Int, navController: NavController, viewModel: ToSDRViewModel) {
    val serviceDetails by viewModel.serviceDetails.collectAsState()
    var showOriginal by remember { mutableStateOf(false) }

    LaunchedEffect(serviceId) {
        viewModel.getServiceDetails(serviceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val url = "https://tosdr.org/service/${serviceDetails?.id}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            navController.context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_rounded_open_in_browser_24),
                            stringResource(R.string.open_in_browser)
                        )
                    }
                },
                title = { Text(serviceDetails?.name ?: stringResource(R.string.service_loading)) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            serviceDetails?.let { service ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Service Header
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = "https://s3.tosdr.org/logos/${service.id}.png",
                                    contentDescription = stringResource(
                                        R.string.service_logo_placeholder,
                                        service.name
                                    ),
                                    modifier = Modifier.size(75.dp),
                                    error = painterResource(id = R.drawable.ic_service_placeholder),
                                    placeholder = painterResource(id = R.drawable.ic_service_placeholder)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Badges Row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                ) {
                                    if (service.is_comprehensively_reviewed) {
                                        Badge(
                                            text = stringResource(R.string.service_reviewed),
                                            icon = rememberVectorPainter(Icons.Rounded.Check),
                                            color = BadgeColors.green
                                        )
                                    }
                                    Badge(
                                        text = stringResource(
                                            R.string.service_grade,
                                            service.rating
                                        ),
                                        icon = painterResource(R.drawable.ic_rounded_shield_24),
                                        color = when (service.rating) {
                                            "A" -> ToSDRColorScheme.gradeA
                                            "B" -> ToSDRColorScheme.gradeB
                                            "C" -> ToSDRColorScheme.gradeC
                                            "D" -> ToSDRColorScheme.gradeD
                                            "E" -> ToSDRColorScheme.gradeE
                                            else -> ToSDRColorScheme.gradeNA
                                        }
                                    )
                                    Badge(
                                        text = stringResource(
                                            R.string.service_point_cnt,
                                            service.points.size
                                        ),
                                        icon = rememberVectorPainter(Icons.Rounded.Warning),
                                        color = BadgeColors.blue
                                    )
                                }
                            }
                        }
                    }

                    // Points Sections
                    if (service.points.isNotEmpty()) {
                        // Group points by classification
                        val groupedPoints = service.points.groupBy { it.case.classification }

                        // Sort points in the order: Blocker, Bad, Good, Neutral
                        val sortedClassifications = listOf("blocker", "bad", "good", "neutral")

                        sortedClassifications.forEach { classification ->
                            groupedPoints[classification]?.let { points ->
                                item {
                                    PointsGroup(
                                        title = when (classification) {
                                            "blocker" -> stringResource(R.string.service_blocker)
                                            "bad" -> stringResource(R.string.service_bad)
                                            "good" -> stringResource(R.string.service_good)
                                            else -> stringResource(R.string.service_neutral)
                                        },
                                        points = points,
                                        onPointClick = { point ->
                                            navController.navigate(
                                                Screen.PointView.createRoute(
                                                    point.id
                                                )
                                            )
                                        },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        original = showOriginal
                                    )
                                }
                            }
                        }
                    }

                    // Add Localization Warning and Toggle
                    if (viewModel.isLocalized()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_rounded_warning_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = stringResource(R.string.service_localization_warning),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                text = stringResource(R.string.service_localization_warning_desc),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stringResource(R.string.service_localization_show_original))
                                    Switch(
                                        checked = showOriginal,
                                        onCheckedChange = { showOriginal = it }
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, icon: Painter, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
