package xyz.ptgms.tosdr.screens.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.lists.getAdaptiveRoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrariesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(stringResource(R.string.libraries_title)) }
            )
        }
    ) { padding ->
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val libs = Libs.Builder()
                .withContext(context)
                .build()

            val uniqueLibs = libs.libraries.distinctBy { it.name }
            items(uniqueLibs) { lib ->
                AttributionList(
                    context,
                    lib,
                    shape = getAdaptiveRoundedCornerShape(
                        index = uniqueLibs.indexOf(lib),
                        lastIndex = uniqueLibs.lastIndex
                    )
                )
            }
        }
    }
}

@Composable
private fun AttributionList(ctx: Context, lib: Library, shape: Shape) {
    Surface(
        shape = shape,
        tonalElevation = 4.dp
    ) {
        Column {
            Text(
                text = lib.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = lib.description ?: stringResource(R.string.libraries_no_description),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
            )
            if (lib.website != null) Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(modifier = Modifier.weight(1f), onClick = {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(lib.website)
                    ctx.applicationContext.startActivity(i, null)
                }) {
                    Text(text = stringResource(R.string.libraries_open_website))
                }
            }
        }
    }
}
