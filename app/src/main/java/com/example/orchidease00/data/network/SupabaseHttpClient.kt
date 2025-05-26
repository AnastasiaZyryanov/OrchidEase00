package com.example.orchidease00.data.network

import android.util.Log
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.network.model.OrchidCatalogDto
import com.example.orchidease00.data.network.model.OrchidImageDto
import com.example.orchidease00.data.network.model.toUiModel
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
                //explicitNulls = false
            })
        }
    }

    suspend fun getOrchidByName(name: String): OrchidCatalogItem {
       val cleanName = name.trim()
       val encodedName = cleanName.encodeURLParameter()
       val fullUrl = "${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog?name=eq.$encodedName&select=*"

       Log.d("Supabase", "🔍 Оригинальное имя: [$name]")
       Log.d("Supabase", "🧹 После trim(): [$cleanName]")
       Log.d("Supabase", "🌐 После encodeURLParameter(): [$encodedName]")
       Log.d("Supabase", "📡 URL запроса: $fullUrl")

       val response: String = client.get(fullUrl) {
           addAuthHeaders()
       }.body()

       Log.d("Supabase", "📦 Сырой ответ от сервера: $response")
       val result = Json.decodeFromString<List<OrchidCatalogDto>>(response)

       return result.firstOrNull()?.toUiModel()
           ?: throw Exception("Orchidea \"$name\" non trovata")
           /*
       val response: String = client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog?name=eq.$encodedName&select=*") {
            addAuthHeaders()
                // parameter("select", "*")
        }.body()
       Log.d("Supabase", "Сырой ответ: $response")
        val result = Json.decodeFromString<List<OrchidCatalogDto>>(response)

        return result.firstOrNull()?.toUiModel()
            ?: throw Exception("Orchidea \"$name\" non trovata")
                        */
    }
   suspend fun getImagesByName(name: String): List<OrchidImageDto> {
       val cleanName = name.trim()
        val encodedName = cleanName.encodeURLParameter()

        val response: String = client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidImages?name=eq.$encodedName&select=*") {
            addAuthHeaders()
                //parameter("select", "*")
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
