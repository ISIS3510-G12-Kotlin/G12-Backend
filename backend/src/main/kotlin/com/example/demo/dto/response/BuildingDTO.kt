package com.example.demo.dto.response

data class BuildingDTO(
    val id: Long,
    val name: String,
    val code: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String?,
    val category: String,
    val places: List<PlaceDTO> = emptyList()
)