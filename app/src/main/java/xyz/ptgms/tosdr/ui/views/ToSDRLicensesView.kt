package xyz.ptgms.tosdr.ui.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R

object ToSDRLicensesView {

    // This is important to prevent a Play Store warning or ban
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRLicensesView(
        modifier: Modifier,
        scope: CoroutineScope,
        navController: NavHostController
    ) {

        val topAppBarState = rememberTopAppBarState()
        val topAppBarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

        val context = LocalContext.current
        Scaffold(topBar = {
            TopAppBar(scrollBehavior = topAppBarBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.open_source_licenses),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { navController.popBackStack() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }) { padding ->
            LazyColumn(
                modifier = modifier
                    .padding(padding)
                    .nestedScroll(topAppBarBehavior.nestedScrollConnection),
            ) {
                val libs = Libs.Builder()
                    .withContext(context)
                    .build()
                // We take libraries but filter out duplicates
                val libraries = libs.libraries.distinctBy { Pair(it.name, it.description) }

                items(libraries.size) { index ->
                    ElevatedCard(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Text(text = libraries[index].name, style = TextStyle(fontSize = 20.sp), modifier = Modifier.padding(8.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        if (libraries[index].developers.isNotEmpty()) {
                            Text(text = libraries[index].developers[0].name ?: "No developer", modifier = Modifier.padding(8.dp))
                        }
                        Divider()
                        Text(text = libraries[index].description ?: "No description", modifier = Modifier.padding(8.dp))

                        Row(modifier = Modifier.padding(8.dp)) {
                            libraries[index].licenses.forEach {
                                ElevatedSuggestionChip(
                                    modifier = Modifier.padding(4.dp),
                                    label = { Text(text = it.name) },
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(it.url)
                                        ContextCompat.startActivity(context, intent, null)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier= Modifier.height(10.dp))
                }
            }
        }
    }
}