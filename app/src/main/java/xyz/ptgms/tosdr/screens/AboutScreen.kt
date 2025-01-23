package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Terms of Service; Didn't Read",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            "\"I have read and agree to the Terms\" is the biggest lie on the web. We aim to fix that.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "How it works",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "We rate and label services based on their terms of service and privacy policies. " +
                    "Our ratings go from Class A to Class E:",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Class A") },
                    supportingContent = { Text("Best Terms of Service: they treat you fairly, respect your rights and will not abuse your data.") }
                )
                ListItem(
                    headlineContent = { Text("Class B") },
                    supportingContent = { Text("Good Terms of Service: they respect your rights but there might be some minor issues.") }
                )
                ListItem(
                    headlineContent = { Text("Class C") },
                    supportingContent = { Text("Fair Terms of Service: they're okay but some issues need your consideration.") }
                )
                ListItem(
                    headlineContent = { Text("Class D") },
                    supportingContent = { Text("Poor Terms of Service: they raise serious concerns.") }
                )
                ListItem(
                    headlineContent = { Text("Class E") },
                    supportingContent = { Text("Very Poor Terms of Service: they don't treat you fairly or respect your rights.") }
                )
            }
        }
    }
}
