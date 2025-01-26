package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.api.models.Team
import xyz.ptgms.tosdr.api.models.TeamMember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(navController: NavController) {
    var team by remember { mutableStateOf<Team?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            team = fetchTeam()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Team") }
            )
        }
    ) { padding ->
        if (team != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Text(
                        "Founders",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(team!!.founders) { member ->
                    TeamMemberCard(member = member)
                }

                item {
                    Text(
                        "Current Team",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(team!!.current) { member ->
                    TeamMemberCard(member = member)
                }

                item {
                    Text(
                        "Past Contributors",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(team!!.past) { member ->
                    TeamMemberCard(member = member)
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
fun TeamMemberCard(member: TeamMember) {
    val uriHandler = LocalUriHandler.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = member.photo,
                    contentDescription = "${member.name}'s photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    error = painterResource(id = R.drawable.ic_service_placeholder),
                    placeholder = painterResource(id = R.drawable.ic_service_placeholder)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (member.title.isNotEmpty()) {
                        Text(
                            text = member.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            MarkdownText(
                markdown = member.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (!member.links.isEmpty) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    member.links.email?.let { email ->
                        IconButton(onClick = { uriHandler.openUri("mailto:$email") }) {
                            Icon(Icons.Rounded.Email, contentDescription = "Email")
                        }
                    }
                    
                    member.links.github?.let { github ->
                        IconButton(onClick = { uriHandler.openUri(github) }) {
                            Icon(painterResource(id = R.drawable.ic_github), contentDescription = "GitHub")
                        }
                    }
                    
                    member.links.website?.let { website ->
                        IconButton(onClick = { uriHandler.openUri(website) }) {
                            Icon(Icons.Rounded.Home, contentDescription = "Website")
                        }
                    }
                    
                    member.links.mastodon?.let { mastodon ->
                        IconButton(onClick = { uriHandler.openUri(mastodon) }) {
                            Icon(painterResource(id = R.drawable.ic_mastodon), contentDescription = "Mastodon")
                        }
                    }
                    
                    member.links.twitter?.let { twitter ->
                        IconButton(onClick = { uriHandler.openUri(twitter) }) {
                            Icon(painterResource(id = R.drawable.ic_twitter), contentDescription = "Twitter")
                        }
                    }
                }
            }
        }
    }
}

private suspend fun fetchTeam(): Team? {
    return try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tosdr.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TeamService::class.java)
        service.getTeam()
    } catch (e: Exception) {
        null
    }
}

interface TeamService {
    @GET("teams")
    suspend fun getTeam(): Team
}
