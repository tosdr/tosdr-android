package xyz.ptgms.tosdr.api.models

import androidx.annotation.Keep

@Keep
data class SearchResponse(
    val services: List<ServiceBasic>
)

@Keep
data class ServiceBasic(
    val id: Int,
    val is_comprehensively_reviewed: Boolean,
    val urls: List<String>,
    val name: String,
    val updated_at: String,
    val created_at: String,
    val slug: String,
    val rating: String
)

@Keep
data class ServiceDetail(
    val id: Int,
    val is_comprehensively_reviewed: Boolean,
    val name: String,
    val updated_at: String,
    val created_at: String,
    val slug: String,
    val rating: String,
    val urls: List<String>,
    val image: String,
    val documents: List<Document>,
    val points: List<Point>
)

@Keep
data class Document(
    val id: Int,
    val name: String,
    val url: String,
    val updated_at: String,
    val created_at: String
)

@Keep
data class Point(
    val id: Int,
    val title: String,
    val source: String,
    val status: String,
    val analysis: String,
    val case: Case,
    val document_id: Int,
    val updated_at: String,
    val created_at: String
)

@Keep
data class Case(
    val id: Int,
    val weight: Int,
    val title: String,
    val localized_title: String?,
    val description: String,
    val updated_at: String,
    val created_at: String,
    val topic_id: Int,
    val classification: String
)

@Keep
data class AppDbEntry(
    val id: Int,
    val name: String,
    val url: String,
    val rating: String
) 