package com.example.demo.service

import com.example.demo.model.Building
import com.example.demo.model.Place
import com.example.demo.repository.BuildingRepository
import com.example.demo.repository.PlaceRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import jakarta.persistence.EntityNotFoundException

@Service
class BuildingService(
    private val buildingRepository: BuildingRepository,
    private val placeRepository: PlaceRepository
) {

    fun findAllBuildings(): List<Building> {
        return buildingRepository.findAll()
    }

    fun findBuildingById(id: Long): Building {
        return buildingRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Building not found with id: $id") }
    }

    fun findBuildingByCode(code: String): Building {
        return buildingRepository.findByCode(code)
            .orElseThrow { EntityNotFoundException("Building not found with code: $code") }
    }

    fun searchBuildings(query: String): List<Building> {
        return buildingRepository.searchBuildings(query)
    }
    
    fun findBuildingsByCategory(category: String): List<Building> {
        return buildingRepository.findByCategory(category)
    }
    
    fun findBuildingsOrderedByDistance(userLat: Double, userLon: Double, page: Int, size: Int): List<Building> {
        val pageable = PageRequest.of(page, size)
        return buildingRepository.findAllOrderedByDistance(userLat, userLon, pageable)
    }

    @Transactional
    fun createBuilding(building: Building): Building {
        return buildingRepository.save(building)
    }

    @Transactional
    fun updateBuilding(id: Long, buildingDetails: Building): Building {
        val building = findBuildingById(id)
        
        building.name = buildingDetails.name
        building.code = buildingDetails.code
        building.description = buildingDetails.description
        building.latitude = buildingDetails.latitude
        building.longitude = buildingDetails.longitude
        building.imageUrl = buildingDetails.imageUrl
        building.category = buildingDetails.category
        building.updatedAt = LocalDateTime.now()
        
        return buildingRepository.save(building)
    }

    @Transactional
    fun deleteBuilding(id: Long) {
        if (!buildingRepository.existsById(id)) {
            throw EntityNotFoundException("Building not found with id: $id")
        }
        buildingRepository.deleteById(id)
    }
    
    fun getPlacesByBuildingId(buildingId: Long): List<Place> {
        return placeRepository.findByBuildingId(buildingId)
    }
    
    @Transactional
    fun addPlaceToBuilding(buildingId: Long, place: Place): Place {
        val building = findBuildingById(buildingId)
        place.building = building
        return placeRepository.save(place)
    }
}