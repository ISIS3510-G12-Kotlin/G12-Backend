package com.example.demo.service

import com.example.demo.model.Place
import com.example.demo.repository.PlaceRepository
import com.example.demo.repository.BuildingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import jakarta.persistence.EntityNotFoundException

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val buildingRepository: BuildingRepository
) {

    fun getAllPlaces(): List<Place> {
        return placeRepository.findAll()
    }

    fun getPlaceById(id: Long): Place {
        return placeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Place not found with id: $id") }
    }

    fun getPlacesByBuilding(buildingId: Long): List<Place> {
        return placeRepository.findByBuildingId(buildingId)
    }

    fun getPlacesByCategory(category: String): List<Place> {
        return placeRepository.findByCategory(category)
    }
    
    fun getPlacesByFloor(floorCode: String): List<Place> {
        return placeRepository.findByFloorContaining(floorCode)
    }

    fun searchPlaces(query: String): List<Place> {
        return placeRepository.searchPlaces(query)
    }

    @Transactional
    fun createPlace(place: Place): Place {
        // Ensure the building exists if provided
        if (place.building != null && place.building?.id != null) {
            val buildingId = place.building?.id!!
            val building = buildingRepository.findById(buildingId)
                .orElseThrow { EntityNotFoundException("Building not found with id: $buildingId") }
            place.building = building
        }
        
        return placeRepository.save(place)
    }

    @Transactional
    fun updatePlace(id: Long, placeDetails: Place): Place {
        val place = getPlaceById(id)
        
        place.name = placeDetails.name
        place.code = placeDetails.code
        place.category = placeDetails.category
        place.floor = placeDetails.floor
        place.coordinates = placeDetails.coordinates
        place.imageUrl = placeDetails.imageUrl
        
        // Update building reference if provided
        if (placeDetails.building != null && placeDetails.building?.id != null) {
            val buildingId = placeDetails.building?.id!!
            val building = buildingRepository.findById(buildingId)
                .orElseThrow { EntityNotFoundException("Building not found with id: $buildingId") }
            place.building = building
        }
        
        place.updatedAt = LocalDateTime.now()
        
        return placeRepository.save(place)
    }

    @Transactional
    fun deletePlace(id: Long) {
        if (!placeRepository.existsById(id)) {
            throw EntityNotFoundException("Place not found with id: $id")
        }
        placeRepository.deleteById(id)
    }
}