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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext

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
                title = { Text("About App") }
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
                val index = uniqueLibs.indexOf(lib)
                AttributionList(
                    context,
                    lib,
                    shape = RoundedCornerShape(
                        topStart = if (index == 0) 24.dp else 8.dp,
                        topEnd = if (index == 0) 24.dp else 8.dp,
                        bottomStart = if (index == uniqueLibs.lastIndex) 24.dp else 8.dp,
                        bottomEnd = if (index == uniqueLibs.lastIndex) 24.dp else 8.dp
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
                text = lib.description ?: "No description",
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
                    Text(text = "Open Website")
                }
            }
        }
    }
}
