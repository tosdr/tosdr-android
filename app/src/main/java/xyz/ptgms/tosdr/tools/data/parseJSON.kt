package xyz.ptgms.tosdr.tools.data

import androidx.annotation.Keep
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.net.HttpURLConnection
import java.net.URL

object ParseJSON {
    fun parseJSON(json: String): Service.Service {
        val klaxon = Klaxon()
        return klaxon.parse<Service.Service>(json)!!
    }

    fun parseSearch(json: String): Search.Search {
        val klaxon = Klaxon()
        return klaxon.parse<Search.Search>(json)!!
    }

    fun parseLibreTranslateJson(language: String, source: String): String {
        val request = TranslateRequest(q = source, source = "en", target = language)
        val json = Klaxon().toJsonString(request)

        // Make request to translate.ptgms.space.
        val url = URL("https://translate.ptgms.space/translate")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.write(json.toByteArray())

        val response = connection.inputStream.bufferedReader().readText()
        val responseJson = Klaxon().parse<LibreTranslateResponse>(response)!!
        return responseJson.translatedText ?: source
    }
}


@Keep
data class LibreTranslateResponse (
    val error: String? = null,
    val translatedText: String? = null
)


@Keep
data class TranslateRequest(
    val q: String,
    val source: String,
    val target: String,
    val format: String = "text"
)

@Keep
class Service {
    @Keep
    data class Service (
        val id: Long? = null,
        val name: String? = null,
        val slug: String? = null,
        val image: String? = null,

        @Json(name = "class")
        val serviceClass: Any? = null,

        @Json(ignored = true)
        val links: Any? = null,
        val points: List<Long>? = null,
        val pointsData: Map<String, PointsDatum>? = null,
        val urls: List<String>? = null,
        val error: Long? = null,
        val message: String? = null,
        val parameters: List<Any?>? = null
    )

    @Keep
    data class PointsDatum (
        val discussion: String,
        val id: Long,
        val needsModeration: Boolean,
        val quoteDoc: String? = null,
        val quoteText: String? = null,
        val services: List<String>,
        val set: String,
        val slug: Any? = null,
        val title: String,
        val topics: List<Any?>,
        val tosdr: Tosdr
    )

    @Keep
    data class Tosdr (
        val binding: Boolean,
        val case: String,
        val point: String,
        val score: Long,
        val tldr: String
    )
}

@Keep
object Search {
    @Keep
    data class Search(
        val error: Long,
        val message: String,
        val parameters: Parameters
    )

    @Keep
    data class Parameters(
        val services: List<Service>
    )

    @Keep
    data class Service(
        val id: Long,

        @Json(name = "is_comprehensively_reviewed")
        val isComprehensivelyReviewed: Boolean,

        val urls: List<String>,
        val name: String,
        val status: Any? = null,

        @Json(name = "updated_at")
        val updatedAt: String,

        @Json(name = "created_at")
        val createdAt: String,

        val slug: String,
        val wikipedia: String,
        val rating: Rating,
        val links: Links
    )

    @Keep
    data class Links(
        val phoenix: Phoenix,
        val crisp: Crisp
    )

    @Keep
    data class Crisp(
        val api: String,
        val service: String,
        val badge: Badge
    )

    @Keep
    data class Badge(
        val svg: String,
        val png: String
    )

    @Keep
    data class Phoenix(
        val service: String,
        val documents: String,

        @Json(name = "new_comment")
        val newComment: String,

        val edit: String
    )

    @Keep
    data class Rating(
        val hex: Long,
        val human: String,
        val letter: String
    )

}
