package edu.oregonstate.cs492.githubsearch.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRepoSearchResults(
    val items: List<GitHubRepo>
)
