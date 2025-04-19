package com.example.demo.controllers

import com.example.demo.model.Building
import com.example.demo.model.Place
import com.example.demo.service.BuildingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buildings")
class BuildingController(private val buildingService: BuildingService) {

    @GetMapping
    fun findAllBuildings(): ResponseEntity<List<Building>> {
        val buildings = buildingService.findAllBuildings()
        return ResponseEntity.ok(buildings)
    }

    @GetMapping("/{id}")
    fun findBuildingById(@PathVariable id: Long): ResponseEntity<Building> {
        val building = buildingService.findBuildingById(id)
        return ResponseEntity.ok(building)
    }

    @GetMapping("/code/{code}")
    fun findBuildingByCode(@PathVariable code: String): ResponseEntity<Building> {
        val building = buildingService.findBuildingByCode(code)
        return ResponseEntity.ok(building)
    }

    @GetMapping("/search")
    fun searchBuildings(@RequestParam query: String): ResponseEntity<List<Building>> {
        val buildings = buildingService.searchBuildings(query)
        return ResponseEntity.ok(buildings)
    }
    
    @GetMapping("/category/{category}")
    fun findBuildingsByCategory(@PathVariable category: String): ResponseEntity<List<Building>> {
        val buildings = buildingService.findBuildingsByCategory(category)
        return ResponseEntity.ok(buildings)
    }
    
    @GetMapping("/nearby")
    fun findNearbyBuildings(
        @RequestParam userLat: Double,
        @RequestParam userLon: Double,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<Building>> {
        val buildings = buildingService.findBuildingsOrderedByDistance(userLat, userLon, page, size)
        return ResponseEntity.ok(buildings)
    }

    @PostMapping
    fun createBuilding(@RequestBody building: Building): ResponseEntity<Building> {
        val createdBuilding = buildingService.createBuilding(building)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBuilding)
    }

    @PutMapping("/{id}")
    fun updateBuilding(
        @PathVariable id: Long, 
        @RequestBody building: Building
    ): ResponseEntity<Building> {
        val updatedBuilding = buildingService.updateBuilding(id, building)
        return ResponseEntity.ok(updatedBuilding)
    }

    @DeleteMapping("/{id}")
    fun deleteBuilding(@PathVariable id: Long): ResponseEntity<Void> {
        buildingService.deleteBuilding(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/{buildingId}/places")
    fun getPlacesByBuildingId(@PathVariable buildingId: Long): ResponseEntity<List<Place>> {
        val places = buildingService.getPlacesByBuildingId(buildingId)
        return ResponseEntity.ok(places)
    }
    
    @PostMapping("/{buildingId}/places")
    fun addPlaceToBuilding(
        @PathVariable buildingId: Long, 
        @RequestBody place: Place
    ): ResponseEntity<Place> {
        val createdPlace = buildingService.addPlaceToBuilding(buildingId, place)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace)
    }
}