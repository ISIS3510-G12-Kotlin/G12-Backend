package com.example.demo.mapper

import com.example.demo.dto.response.BuildingDTO
import com.example.demo.dto.response.PlaceDTO
import com.example.demo.model.Building
import com.example.demo.model.Place

// Extension function to convert Building to BuildingDTO
fun Building.toDTO(): BuildingDTO {
    return BuildingDTO(
        id = this.id,
        name = this.name,
        code = this.code,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.imageUrl,
        category = this.category,
        places = this.places.map { it.toDTO() }
    )
}

// Extension function to convert Place to PlaceDTO
fun Place.toDTO(): PlaceDTO {
    return PlaceDTO(
        id = this.id,
        name = this.name,
        code = this.code,
        category = this.category,
        floor = this.floor,
        coordinates = this.coordinates,
        imageUrl = this.imageUrl,
        buildingId = this.building?.id
    )
}