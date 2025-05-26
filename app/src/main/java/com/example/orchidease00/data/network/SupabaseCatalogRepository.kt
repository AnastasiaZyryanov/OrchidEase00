package com.example.orchidease00.data.network

import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.network.model.OrchidCatalogDto
import com.example.orchidease00.data.network.model.OrchidImageDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseCatalogRepository {

    suspend fun fetchCatalog(): List<OrchidCatalogItem> = withContext(Dispatchers.IO) {
        // Ottiene lista delle orchidee da Supabase
        val orchidDtos: List<OrchidCatalogDto> =
            SupabaseHttpClient.client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog") {
                addAuthHeaders()
            }.body()

        // Ottiene le imagini
        val imageDtos: List<OrchidImageDto> =
            SupabaseHttpClient.client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidImages") {
                addAuthHeaders()
            }.body()

        // Assoc. tra imagine e nome
        val imageMap = imageDtos.associateBy { it.name }

        // Unione di dto con image in OrchidCatalogItem
        orchidDtos.map { dto ->
            OrchidCatalogItem(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                care = dto.care,
                imageUrl = imageMap[dto.name]?.imageUrl ?: ""
            )
        }
    }
    suspend fun getOrchidByName(name: String): OrchidCatalogItem {
        return SupabaseHttpClient.getOrchidByName(name)
    }

    suspend fun getImagesByName(name: String): List<OrchidImageDto> {
        return SupabaseHttpClient.getImagesByName(name)
    }
}





