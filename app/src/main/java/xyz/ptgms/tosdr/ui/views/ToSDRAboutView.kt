package xyz.ptgms.tosdr.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.ui.views.elements.ExpandableCard
import xyz.ptgms.tosdr.ui.views.elements.TextWithSource

object ToSDRAboutView {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRAboutView(modifier: Modifier, scope: CoroutineScope, drawerState: DrawerState) {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.about),
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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                Text(
                    text = stringResource(R.string.about_tos_dr),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = stringResource(R.string.about_tosdr_description),
                    modifier = Modifier.padding(8.dp)
                )

                ExpandableCard(
                    title = stringResource(R.string.about_card_about_title),
                    description = TextWithSource(
                        stringResource(R.string.about_card_about_description),
                        "https://github.com/tosdr/tosdr-android",
                        stringResource(
                            R.string.visit_source
                        )
                    ),
                    modifier = Modifier.padding(8.dp)
                )

                ExpandableCard(
                    title = stringResource(R.string.about_tos_dr), description = TextWithSource(
                        stringResource(R.string.about_card_tosdr_description),
                        "https://tosdr.org/en/about",
                        stringResource(
                            R.string.visit_website
                        )
                    ), modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}