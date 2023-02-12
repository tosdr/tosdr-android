package xyz.ptgms.tosdr.ui.views

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeBackground
import xyz.ptgms.tosdr.tools.data.GradeToHumanReadable.gradeForeground
import xyz.ptgms.tosdr.tools.data.SearchResult
import xyz.ptgms.tosdr.tools.data.api.API.searchPage
import kotlin.concurrent.thread

object ToSDRSearchView : ViewModel() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRView(modifier: Modifier, navHostController: NavHostController, scope: CoroutineScope, drawerState: DrawerState) {
        val context: Context = LocalContext.current

        val topAppBarState = rememberTopAppBarState()
        val topAppBarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

        val sharedPreference =
            LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hideGrade = sharedPreference.getBoolean("hideNoGrade", false)
        val hideNotReviewed = sharedPreference.getBoolean("hideNotReviewed", false)
        var value by rememberSaveable { mutableStateOf("") }
        var status by rememberSaveable { mutableStateOf("") }
        val searchResult = remember { mutableStateListOf<SearchResult>() }
        var progress by remember { mutableStateOf(0.0f) }
        Scaffold(topBar = {
            TopAppBar(
                scrollBehavior = topAppBarBehavior,
                title = {
                    Text(
                        text = stringResource(id = R.string.search),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                }
            )
        }) { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    //.verticalScroll(rememberScrollState())
                    .padding(padding)
                    .nestedScroll(topAppBarBehavior.nestedScrollConnection)
            ) {
                // Website name input
                //LinearProgressIndicator(progress = progress, modifier = Modifier.padding(8.dp).fillMaxWidth())
                item { Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text(stringResource(R.string.search_label)) },
                        placeholder = { Text(stringResource(R.string.search_label_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        },
                        trailingIcon = {
                            if (value != "") {
                                if (progress != 0f)
                                    CircularProgressIndicator()
                                else
                                    IconButton(onClick = { value = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.round_backspace_24),
                                            contentDescription = stringResource(R.string.clear)
                                        )
                                    }
                            } else Spacer(modifier = Modifier)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            status = context.getString(R.string.loading)
                            progress = 0.1f
                            searchResult.clear()
                            thread {
                                progress = 0.3f
                                try {
                                    val data = searchPage(value, hideGrade, hideNotReviewed)
                                    progress = 0.7f

                                    // Set the report in the UI thread
                                    viewModelScope.launch(Dispatchers.Main) {
                                        searchResult.addAll(data)
                                        progress = 0f
                                        status = context.getString(R.string.done)
                                    }
                                } catch (e: Exception) {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show()
                                        progress = 0f
                                        status = context.getString(R.string.done)
                                    }
                                    Log.e("ToSDRSearchView", "Error while searching", e)
                                    return@thread
                                }
                            }
                        }),
                        maxLines = 1
                    )
                }
                }

                items(searchResult.size) {index ->
                    SearchCard(searchResult[index], navHostController)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchCard(SearchResult: SearchResult, navController: NavHostController) {
        ElevatedCard(modifier = Modifier.padding(8.dp), shape = CardDefaults.shape, onClick = {
            if (SearchResult.grade == "N/A") {
                navController.navigate("details/${SearchResult.page}/None")
            } else {
                navController.navigate("details/${SearchResult.page}/${SearchResult.grade}")
            }

        }) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(SearchResult.icon)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder),
                    contentDescription = stringResource(R.string.details_icon, SearchResult.name),
                    modifier = Modifier.clip(shape = RoundedCornerShape(8.dp))
                        .size(48.dp)
                        .padding(8.dp)
                )
                Text(text = SearchResult.name, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier
                    .weight(1f)
                    .padding(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = gradeBackground(SearchResult.grade), contentColor = gradeForeground(SearchResult.grade)),
                    modifier = Modifier
                        .width(65.dp)
                        .padding(12.dp)
                ) {
                    Text(text = SearchResult.grade, color = gradeForeground(SearchResult.grade), textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp))
                }

            }
        }
        //ReportCard(title = SearchResult.Name, grade = SearchResult.Grade, page = SearchResult.Page, modifier = Modifier.padding(8.dp))
    }
}