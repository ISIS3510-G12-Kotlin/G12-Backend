package com.example.demo.dto.response

data class PlaceDTO(
    val id: Long,
    val name: String,
    val code: String?,
    val category: String?,
    val floor: String?,
    val coordinates: String?,
    val imageUrl: String?,
    val buildingId: Long?
)