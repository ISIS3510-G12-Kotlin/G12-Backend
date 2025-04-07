package com.example.demo.service

import com.example.demo.model.Place
import com.example.demo.repository.PlaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PlaceService(
    private val placeRepository: PlaceRepository
) {
    @Transactional(readOnly = true)
    fun getAllPlaces(): List<Place> = placeRepository.findAll()

    @Transactional(readOnly = true)
    fun getPlaceById(id: Long): Place = 
        placeRepository.findById(id)
            .orElseThrow { NoSuchElementException("Place not found with id: $id") }

    @Transactional(readOnly = true)
    fun getPlacesByBuilding(buildingId: Long): List<Place> = 
        placeRepository.findByBuildingId(buildingId)

    @Transactional(readOnly = true)
    fun getPlacesByCategory(category: String): List<Place> = 
        placeRepository.findByCategory(category)

    @Transactional(readOnly = true)
    fun searchPlaces(query: String): List<Place> = 
        placeRepository.searchPlaces(query)

    @Transactional
    fun createPlace(place: Place): Place = 
        placeRepository.save(place)

    @Transactional
    fun updatePlace(id: Long, updatedPlace: Place): Place {
        val existingPlace = getPlaceById(id)
        val newPlace = existingPlace.copy(
            name = updatedPlace.name,
            code = updatedPlace.code,
            category = updatedPlace.category,
            distance = updatedPlace.distance,
            coordinates = updatedPlace.coordinates,
            imageUrl = updatedPlace.imageUrl,
            building = updatedPlace.building,
            updatedAt = LocalDateTime.now()
        )
        return placeRepository.save(newPlace)
    }

    @Transactional
    fun deletePlace(id: Long) {
        if (placeRepository.existsById(id)) {
            placeRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Place not found with id: $id")
        }
    }
}