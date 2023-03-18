package xyz.ptgms.tosdr.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.play.core.review.ReviewManagerFactory
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
        val sharedPreference =
            LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val review = sharedPreference.getBoolean("review", false)

        val context = LocalContext.current
        if (!review) {
            // Set the review to false
            sharedPreference.edit().putBoolean("review", true).apply()
            // Show dialog to ask for review
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.review_title)) },
                text = {
                    Text(stringResource(R.string.review_description))
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Set the review to false
                        sharedPreference.edit().putBoolean("review", false).apply()
                        // Show dialog to ask for review
                        val manager = ReviewManagerFactory.create(context)
                        val request = manager.requestReviewFlow()
                        request.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // We got the ReviewInfo object
                                Toast.makeText(context,
                                    context.getText(R.string.thank_you), Toast.LENGTH_SHORT).show()
                            } else {
                                // There was some problem, log or handle the error code.
                                Toast.makeText(context,
                                    context.getText(R.string.review_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // Set the review to false
                        sharedPreference.edit().putBoolean("review", false).apply()
                    }) {
                        Text(stringResource(id = android.R.string.cancel))
                    }
                },
            )
        }

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