package com.example.orchidease00.data.remote

import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import com.example.orchidease00.data.remote.dto.OrchidCatalogDto
import com.example.orchidease00.data.remote.dto.OrchidImageDto
import com.example.orchidease00.data.remote.dto.toUiModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.http.encodeURLParameter


object SupabaseHttpClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                })
        }
    }

    suspend fun getOrchidByName(name: String): OrchidCatalogItem {
       val cleanName = name.trim()
       val encodedName = cleanName.encodeURLParameter()
       val fullUrl = "${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog?name=eq.$encodedName&select=*"

       val response: String = client.get(fullUrl) {
           addAuthHeaders()
       }.body()

       val result = Json.decodeFromString<List<OrchidCatalogDto>>(response)

       return result.firstOrNull()?.toUiModel()
           ?: throw Exception("Orchidea \"$name\" non trovata")
             }
   suspend fun getImagesByName(name: String): List<OrchidImageDto> {
       val cleanName = name.trim()
        val encodedName = cleanName.encodeURLParameter()

        val response: String = client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidImages?name=eq.$encodedName&select=*") {
            addAuthHeaders()
        }.body()

        return Json.decodeFromString(response)
    }

    suspend fun getOrchidCatalog(): List<OrchidCatalogItem> {
        return try {
            client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog") {
                addAuthHeaders()
                parameter("select", "*")
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


}
