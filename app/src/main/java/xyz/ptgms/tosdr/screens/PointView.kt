package xyz.ptgms.tosdr.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.points.PointsRow
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointView(pointId: Int, navController: NavController, viewModel: ToSDRViewModel) {
    val context = LocalContext.current
    val pointDetails by viewModel.pointDetails.collectAsState()

    LaunchedEffect(pointId) {
        viewModel.getPointDetails(pointId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { 
                    Text("Point Details")
                }
            )
        }
    ) { padding ->
        if (pointDetails != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    Text("Point Details", style = MaterialTheme.typography.titleSmall)
                    PointsRow(point = pointDetails!!)
                }

                if (pointDetails!!.case.localized_title != null) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Original Point", style = MaterialTheme.typography.titleSmall)
                        PointsRow(point = pointDetails!!, original = true)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (pointDetails!!.case.description.isNotEmpty()) {
                    item {
                        SectionCard(title = "Description") {
                            Text(pointDetails!!.case.description)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    SectionCard(title = "Actions") {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("https://edit.tosdr.org/points/${pointDetails!!.id}")
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(painterResource(R.drawable.ic_rounded_open_in_browser_24), contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open on ToS;DR")
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
} 