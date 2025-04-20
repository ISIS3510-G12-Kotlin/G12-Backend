package com.example.demo.dto.response

data class EventDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val type: String?,
    val startTime: String,
    val endTime: String,
    val locationId: Long?,
    val locationName: String?
)