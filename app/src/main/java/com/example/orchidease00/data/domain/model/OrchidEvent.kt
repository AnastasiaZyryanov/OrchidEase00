package com.example.orchidease00.data.domain.model

import java.time.LocalDate

data class OrchidEvent(
    val id: Int,
    val orchidName: String,
    val date: LocalDate,
    val description: String,
    val isNotified: Boolean = false
)

