package com.example.orchidease00.data.remote

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.*
import com.example.orchidease00.BuildConfig


object SupabaseConfig {
    const val BASE_URL = "https://hzumeqyvreugckkijjgw.supabase.co"
    const val API_KEY = BuildConfig.SUPABASE_KEY
    const val BUCKET = "orchid-images"
    }
fun HttpRequestBuilder.addAuthHeaders() {
    header("apikey", SupabaseConfig.API_KEY)
    header("Authorization", "Bearer ${SupabaseConfig.API_KEY}")
    header("Accept", "application/json")
    header("Content-Type", "application/json")

}
