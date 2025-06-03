package com.example.orchidease00.data.remote

import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import com.example.orchidease00.data.remote.dto.OrchidCatalogDto
import com.example.orchidease00.data.remote.dto.OrchidImageDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseCatalogRepository {

    suspend fun fetchCatalog(): List<OrchidCatalogItem> = withContext(Dispatchers.IO) {
        val orchidDtos: List<OrchidCatalogDto> =
            SupabaseHttpClient.client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidCatalog") {
                addAuthHeaders()
            }.body()

        val imageDtos: List<OrchidImageDto> =
            SupabaseHttpClient.client.get("${SupabaseConfig.BASE_URL}/rest/v1/OrchidImages") {
                addAuthHeaders()
            }.body()

        val imageMap = imageDtos.associateBy { it.name }

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





