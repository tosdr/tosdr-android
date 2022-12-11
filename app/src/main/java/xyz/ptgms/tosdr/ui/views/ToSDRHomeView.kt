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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.ui.views.elements.ExpandableCard
import xyz.ptgms.tosdr.ui.views.elements.TextWithSource

object ToSDRHomeView {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRHomeView(modifier: Modifier, scope: CoroutineScope, drawerState: DrawerState) {
        val topAppBarState = rememberTopAppBarState()
        val topAppBarBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
        Scaffold(topBar = {
            TopAppBar(
                scrollBehavior = topAppBarBehavior,
                title = {
                    Text(
                        text = stringResource(id = R.string.home),
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
                    .nestedScroll(topAppBarBehavior.nestedScrollConnection)
                    .padding(padding)
            ) {
                Text(
                    text = stringResource(R.string.home_welcome),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )
                ExpandableCard(
                    title = stringResource(R.string.home_card_what_is_this_title),
                    description = stringResource(
                        R.string.home_card_what_is_this_description
                    ),
                    modifier = Modifier.padding(8.dp)
                )
                ExpandableCard(
                    title = stringResource(R.string.home_card_how_to_use_title),
                    description = stringResource(
                        R.string.home_card_how_to_use_description
                    ),
                    modifier = Modifier.padding(8.dp)
                )
                ExpandableCard(
                    title = stringResource(R.string.home_card_like_title),
                    description = stringResource(
                        R.string.home_card_like_description
                    ),
                    modifier = Modifier.padding(8.dp)
                )
                ExpandableCard(
                    title = stringResource(R.string.home_card_legal_title),
                    description = TextWithSource(
                        stringResource(R.string.home_card_legal_description),
                        "https://tosdr.org/legal",
                        null
                    ),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}