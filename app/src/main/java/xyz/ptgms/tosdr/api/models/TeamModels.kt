package xyz.ptgms.tosdr.api.models

import androidx.annotation.Keep

@Keep
data class TeamMemberLinks(
    val email: String?,
    val github: String?,
    val twitter: String?,
    val website: String?,
    val mastodon: String?
) {
    val isEmpty: Boolean
        get() = email == null && github == null && twitter == null && website == null && mastodon == null
}

@Keep
data class TeamMember(
    val photo: String,
    val name: String,
    val title: String,
    val description: String,
    val links: TeamMemberLinks
)

@Keep
data class Team(
    val founders: List<TeamMember>,
    val current: List<TeamMember>,
    val past: List<TeamMember>
) 