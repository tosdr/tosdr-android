package xyz.ptgms.tosdr.tools.data.api

import android.os.StrictMode
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import com.deepl.api.ConnectionException
import com.deepl.api.Formality
import com.deepl.api.QuotaExceededException
import com.deepl.api.TextResult
import com.deepl.api.TextTranslationOptions
import com.deepl.api.Translator
import xyz.ptgms.tosdr.tools.data.ParseJSON
import xyz.ptgms.tosdr.tools.data.ParseJSON.parseLibreTranslateJson
import xyz.ptgms.tosdr.tools.data.Point
import xyz.ptgms.tosdr.tools.data.SearchResult
import xyz.ptgms.tosdr.tools.data.TosDR
import java.net.URL

object API {
    fun deepLTranslation(original: Point, apiKey: String): Point {
        // Return some default values to not waste API Calls
        if (original.title.value == "Generated through the annotate view")
            original.title.value = ""

        // Get the language code
        val locale = Locale.current.language

        // No need to translate english / If the language code is not supported, return original to not waste API Calls
        if (locale.startsWith("en") || !xyz.ptgms.tosdr.tools.data.Locale.languages.contains(locale)) {
            return original
        }

        val translationQueue = mutableListOf<String>()
        val translatedStrings = mutableListOf<Int>()

        val translateFinal = mutableListOf("", "", "")

        if (original.title.value != "") {
            Log.d("DeepL", original.title.value)
            translationQueue.add(original.title.value)
            translatedStrings.add(0)
        }

        if (original.description.value != "") {
            Log.d("DeepL", original.title.value)
            translationQueue.add(original.description.value)
            translatedStrings.add(1)
        }

        // We do not translate quotes
        // 1: Saves API Calls
        // 2: Display quotes in the original language

        val translator = Translator(apiKey)
        try {
            val results: List<TextResult> = translator.translateText(
                translationQueue,
                null,
                locale,
                TextTranslationOptions().setFormality(Formality.Less)
            )

            results.forEach { result ->
                translatedStrings.forEach {
                    when (it) {
                        0 -> translateFinal[0] = result.text
                        1 -> translateFinal[1] = result.text
                    }
                }
            }
            original.title.value = translateFinal[0]
            original.description.value = translateFinal[1]

            original.translated = true

            return original
        } catch (e: IllegalArgumentException) {
            Log.e("DeepL", "Error while translating: \"${e.message}\"")
            Log.e("DeepL", "Original: ${original.title.value}")
            Log.e("DeepL", "\"${original.description.value}\"")
            Log.e("DeepL", "\"${original.tlDr.value}\"")
            return original
        } catch (e: ConnectionException) {
            Log.e("DeepL", "Error while translating: \"${e.message}\"")
            return original
        } catch (e: QuotaExceededException) {
            return original
        }
    }

    fun libreTranslate(original: Point): Point {
        // Return some default values to not waste API Calls
        if (original.title.value == "Generated through the annotate view")
            original.title.value = ""

        // Get the language code
        val locale = Locale.current.language

        // No need to translate english
        if (locale.startsWith("en"))
            return original

        if (!xyz.ptgms.tosdr.tools.data.Locale.languages_libre.contains(locale)) {
            return original
        }

        val translationQueue = mutableListOf<String>()
        val translatedStrings = mutableListOf<Int>()

        val translateFinal = mutableListOf("", "", "")

        if (original.title.value != "") {
            translationQueue.add(original.title.value)
            translatedStrings.add(0)
        }

        if (original.description.value != "") {
            translationQueue.add(original.description.value)
            translatedStrings.add(1)
        }

        // We do not translate quotes
        // 1: Saves API Calls
        // 2: Display quotes in the original language

        try {
            val results: MutableList<String> = mutableListOf()

            translationQueue.forEach {
                // Translate logic
                results.add(parseLibreTranslateJson(locale, it))
            }

            results.forEach { result ->
                translatedStrings.forEach {
                    when (it) {
                        0 -> translateFinal[0] = result
                        1 -> translateFinal[1] = result
                    }
                }
            }
            original.title.value = translateFinal[0]
            original.description.value = translateFinal[1]

            original.translated = true

            return original
        } catch (e: Exception) {
            Log.e("LibreTranslate", "Error while translating: \"${e.message}\"")
            Log.e("LibreTranslate", "Original: ${original.title.value}")
            Log.e("LibreTranslate", "\"${original.description.value}\"")
            Log.e("LibreTranslate", "\"${original.tlDr.value}\"")
            return original
        }
    }
    fun getToSDR(service: String, grade: String, localisePoints: Boolean): TosDR?{
        // Request: https://api.tosdr.org/service/v1/
        // Parameter: service: String
        val policy = StrictMode.ThreadPolicy.Builder().permitNetwork().build()
        StrictMode.setThreadPolicy(policy)
        val url = "https://tosdr.org/api/v1/service/$service.json"
        val response = URL(url).readText()
        Log.d("Api", response)
        val json = ParseJSON.parseJSON(response)
        if ((json.error ?: 256).toInt() != 256) {
            return null
        }
        val name = json.name ?: "Unknown"
        val points = json.pointsData
        val pointList = json.points
        val pointsList = mutableListOf<Point>()
        if (pointList != null && points != null) {
            //Log.d("Api", "Points: $pointList")
            //Log.d("Api", "Points: $points")
            points.forEach { point ->
                //val point = points[i.toString()] ?: continue
                val title = point.value.title
                val tldr =
                    if (point.value.tosdr.tldr != "Generated through the annotate view") point.value.tosdr.tldr else ""
                val description = point.value.tosdr.case
                val quote = point.value.quoteText
                val link = point.value.discussion
                pointsList.add(
                    Point(
                        mutableStateOf(title),
                        mutableStateOf(tldr),
                        mutableStateOf(description),
                        quote ?: "",
                        point.value.tosdr.point,
                        link,
                        Locale.current.language=="en" || !localisePoints
                    )
                )
            }
        }

        // Check if type of serviceClass is boolean
        val rating: String = if (json.serviceClass is Boolean) {
            "N/A"
        } else {
            (json.serviceClass ?: "N/A").toString()
        }
        //val rating: String = (json.serviceClass ?: "N/A") as String
        return TosDR(
            name,
            service,
            json.image ?: "",
            grade,
            pointsList,
            rating != "N/A",
            json.urls ?: listOf(),
        )
    }

    fun searchPage(
        query: String,
        hideGrade: Boolean,
        hideNotReviewed: Boolean
    ): List<SearchResult> {
        // Request: https://api.tosdr.org/search/v4/?query=
        // Parameter: query: String
        val policy = StrictMode.ThreadPolicy.Builder().permitNetwork().build()
        StrictMode.setThreadPolicy(policy)
        val url = "https://api.tosdr.org/search/v4/?query=$query"
        val response = URL(url).readText()
        Log.d("Api", response)
        val json = ParseJSON.parseSearch(response)

        if (json.error.toInt() != 256) {
            return mutableListOf()
        }

        val searchResults = mutableListOf<SearchResult>()

        for (i in json.parameters.services.indices) {
            if (!json.parameters.services[i].isComprehensivelyReviewed && hideNotReviewed) {
                continue
            }
            val service = json.parameters.services[i]
            val name = service.name
            val grade = service.rating.letter
            if (grade == "N/A" && hideGrade) {
                continue
            }
            searchResults.add(SearchResult(name, service.id.toInt().toString(), grade))
        }

        return searchResults

    }
}