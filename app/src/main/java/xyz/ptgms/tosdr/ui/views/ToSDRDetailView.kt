package xyz.ptgms.tosdr.ui.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.tools.data.api.API
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeBackground
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeForeground
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeToHuman
import xyz.ptgms.tosdr.tools.data.Point
import xyz.ptgms.tosdr.tools.data.TosDR
import xyz.ptgms.tosdr.ui.views.elements.ReportCard
import kotlin.concurrent.thread

object ToSDRDetailView : ViewModel() {


    // Localise each point in mutable so it updates over time :)
    private fun localisePoints(
        original: MutableList<Point>,
        report: MutableState<TosDR>,
        apiKey: String,
        navController: NavHostController,
        deepLSelected: Boolean
    ) {
        // Go through points with index
        thread {
            for (i in original.indices) {
                if (navController.currentBackStackEntry?.destination?.route?.startsWith("details/") == false) {
                    Log.w("Translation", "Popped back, cancel translation!")
                    return@thread
                }
                // Get translation in background thread to slowly update
                var translatedPoint: Point
                if (deepLSelected && apiKey != "") {
                    translatedPoint = API.deepLTranslation(
                        original[i], apiKey = apiKey
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        report.value.points[i] = translatedPoint
                    }
                } else if (!deepLSelected) {
                    translatedPoint = API.libreTranslate(
                        original[i]
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        report.value.points[i] = translatedPoint
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRDetailView(
        modifier: Modifier,
        page: String,
        grade: String,
        navController: NavHostController,
        preview: Boolean = false
    ) {
        val sharedPreference =
            LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val localisePoints = sharedPreference.getBoolean("localisePoints", false)
        val deepLSelected = sharedPreference.getBoolean("deepLSelected", false)

        val topAppBarState = rememberTopAppBarState()
        val topAppBarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

        val gradeMode = remember { mutableStateOf(true) }

        val openDialog = remember { mutableStateOf(false) }
        val verifiedDialog = remember { mutableStateOf(false) }
        val unverifiedDialog = remember { mutableStateOf(false) }

        val context = LocalContext.current

        val points = remember {
            mutableListOf<Point>(
            )
        }

        val report = remember {
            mutableStateOf(
                TosDR(
                    name = "",
                    id = "",
                    icon = "",
                    grade = "",
                    points = points,
                    reviewed = false,
                    urls = listOf()
                )
            )
        }

        if (preview)
            report.value = TosDR(
                name = "Example",
                id = "example",
                icon = "https://tosdr.org/images/services/1.png",
                grade = "A",
                points = mutableListOf(
                    Point(
                        title = remember { mutableStateOf("Example Title") },
                        description = remember { mutableStateOf("Example Point") },
                        quote = "This is an example quote",
                        tlDr = remember { mutableStateOf("Example tldr") },
                        type = "good",
                        links = ""
                    ),
                    Point(
                        title = remember { mutableStateOf("Example Title") },
                        description = remember { mutableStateOf("Example Point") },
                        quote = "This is an example quote",
                        tlDr = remember { mutableStateOf("Example tldr") },
                        type = "blocker",
                        links = ""
                    )
                ),
                reviewed = true,
                urls = listOf("https://tosdr.org")
            )

        Dialog(
            Icons.Rounded.Check, stringResource(R.string.detail_review_true_title), stringResource(
                R.string.detail_review_true_description
            ), Color.Green, verifiedDialog
        )
        Dialog(
            Icons.Rounded.Warning,
            stringResource(R.string.detail_review_false_title),
            stringResource(
                R.string.detail_review_false_description
            ),
            Color.Red,
            unverifiedDialog
        )

        // Alert dialog
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(stringResource(R.string.details_known_urls).format(report.value.name)) },
                text = {
                    Column(modifier.fillMaxWidth()) {
                        var text = ""
                        report.value.urls.forEach {
                            text += "$it\n"
                        }
                        Text(
                            text, modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                },
            )
        }

        val apiKey = sharedPreference.getString("deeplKey", "") ?: ""

        thread {
            try {
                val data = API.getToSDR(page, grade, localisePoints)!!
                // Set the report in the UI thread
                viewModelScope.launch(Dispatchers.Main) {
                    report.value = data
                    if (localisePoints) {
                        localisePoints(
                            original = report.value.points,
                            report = report,
                            apiKey = apiKey,
                            navController= navController,
                            deepLSelected = deepLSelected
                        )
                    }
                }

            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.network_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@thread
            }
        }

        Scaffold(
            //modifier = Modifier.nestedScroll(TopAppBarDefaults.enterAlwaysScrollBehavior().nestedScrollConnection),
            topBar = {
                Column {
                    LargeTopAppBar(scrollBehavior = topAppBarBehavior,
                        title = {
                            if (report.value.name == "") {
                                Text(
                                    text = stringResource(R.string.loading),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(report.value.icon),
                                        contentDescription = stringResource(R.string.details_icon).format(
                                            report.value.name
                                        ),
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(shape = RoundedCornerShape(8.dp))
                                    )
                                    Text(
                                        text = report.value.name,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        })

                }
            }) { padding ->
            // Display report once it's loaded
            if (report.value.name != "") {
                LazyColumn(
                    modifier = modifier
                        //.verticalScroll(rememberScrollState())
                        .padding(padding)
                        .nestedScroll(topAppBarBehavior.nestedScrollConnection),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        //val context = LocalContext.current
                        Column(
                            modifier = Modifier
                        ) {
                            Row(
                                modifier = Modifier
                                    //.fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Spacer(modifier = Modifier.size(12.dp))
                                if (report.value.reviewed) {
                                    ElevatedSuggestionChip(
                                        colors = SuggestionChipDefaults.elevatedSuggestionChipColors(
                                            containerColor = Color(0xFF00C853),
                                            labelColor = Color.White,
                                            iconContentColor = Color.White
                                        ),
                                        label = { Text(text = stringResource(R.string.details_reviewed)) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = stringResource(R.string.details_reviewed)
                                            )
                                        },
                                        onClick = {
                                            verifiedDialog.value = true
                                        }
                                    )
                                } else {
                                    ElevatedSuggestionChip(
                                        colors = SuggestionChipDefaults.elevatedSuggestionChipColors(
                                            containerColor = Color(0xFFC80000),
                                            labelColor = Color.White,
                                            iconContentColor = Color.White
                                        ),
                                        label = { Text(text = stringResource(R.string.details_not_reviewed)) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Warning,
                                                contentDescription = stringResource(R.string.details_not_reviewed)
                                            )
                                        },
                                        onClick = {
                                            unverifiedDialog.value = true
                                        }
                                    )
                                }
                                if (report.value.grade != "None") {
                                    ElevatedSuggestionChip(
                                        colors = SuggestionChipDefaults.elevatedSuggestionChipColors(
                                            containerColor = gradeBackground(report.value.grade),
                                            labelColor = gradeForeground(report.value.grade),
                                            iconContentColor = gradeForeground(report.value.grade)
                                        ),
                                        label = {
                                            if (gradeMode.value) {
                                                Text(
                                                    text = stringResource(R.string.details_grade).format(
                                                        report.value.grade
                                                    )
                                                )
                                            } else {
                                                Text(text = gradeToHuman(report.value.grade))
                                            }
                                        },
                                        onClick = {
                                            gradeMode.value = !gradeMode.value
                                        }
                                    )
                                }
                                ElevatedSuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = stringResource(R.string.details_cases).format(
                                                report.value.points.size
                                            )
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Rounded.Warning,
                                            contentDescription = stringResource(R.string.details_cases).format(
                                                report.value.points.size
                                            )
                                        )
                                    }
                                )
                                ElevatedSuggestionChip(
                                    onClick = {
                                        val url = "https://tosdr.org/en/service/${report.value.id}"
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(url)
                                        startActivity(context, intent, null)
                                    },
                                    label = { Text(text = stringResource(R.string.details_view_on_page)) },
                                    icon = {
                                        Icon(
                                            Icons.Rounded.Share,
                                            contentDescription = stringResource(R.string.details_view_on_page)
                                        )
                                    }
                                )
                                ElevatedSuggestionChip(
                                    onClick = {
                                        // Show alert with all the URLs
                                        openDialog.value = true
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(R.string.details_saved_urls).format(
                                                report.value.urls.size
                                            )
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Rounded.List, contentDescription = "URLs"
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                            }
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                    items(count = report.value.points.size) { index ->
                        val it = report.value.points[index]
                        ReportCard(
                            title = it.title.value,
                            description = it.description.value,
                            quote = it.quote,
                            type = it.type,
                            link = it.links,
                            translation = it.translated,
                            modifier = Modifier.padding(
                                top = 6.dp,
                                bottom = 6.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(id = R.string.loading),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

    }

    @Composable
    fun Dialog(
        icon: ImageVector,
        title: String,
        description: String,
        tint: Color,
        visible: MutableState<Boolean>
    ) {
        if (visible.value) {
            AlertDialog(
                icon = {
                    Icon(
                        tint = tint,
                        imageVector = icon,
                        modifier = Modifier.size(48.dp),
                        contentDescription = title
                    )
                },
                onDismissRequest = { visible.value = false },
                title = { Text(title) },
                text = { Text(description) },
                confirmButton = {
                    TextButton(onClick = { visible.value = false }) {
                        Text(stringResource(android.R.string.ok))
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    val context = LocalContext.current
    ToSDRDetailView.ToSDRDetailView(
        modifier = Modifier,
        page = "182",
        grade = "E",
        navController = NavHostController(context),
        preview = true
    )
}