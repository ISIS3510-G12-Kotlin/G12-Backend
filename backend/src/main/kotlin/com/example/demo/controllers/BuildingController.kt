// File: com/example/demo/controllers/BuildingController.kt
package com.example.demo.controllers

import com.example.demo.dto.response.BuildingDTO
import com.example.demo.dto.response.PlaceDTO
import com.example.demo.mapper.toDTO
import com.example.demo.model.Building
import com.example.demo.model.Place
import com.example.demo.service.BuildingService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buildings")
class BuildingController(
    private val buildingService: BuildingService,
    private val meterRegistry: MeterRegistry // Inject MeterRegistry
) {

    @GetMapping
    fun findAllBuildings(): ResponseEntity<List<BuildingDTO>> {
        // Track metric
        recordMetric("/api/buildings")
        
        val buildings = buildingService.findAllBuildings()
        return ResponseEntity.ok(buildings.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun findBuildingById(@PathVariable id: Long): ResponseEntity<BuildingDTO> {
        // Track metric with building ID tag
        recordMetric("/api/buildings/${id}", buildingId = id)
        
        val building = buildingService.findBuildingById(id)
        return ResponseEntity.ok(building.toDTO())
    }

    @GetMapping("/code/{code}")
    fun findBuildingByCode(@PathVariable code: String): ResponseEntity<BuildingDTO> {
        // Track metric
        recordMetric("/api/buildings/code/${code}")
        
        val building = buildingService.findBuildingByCode(code)
        return ResponseEntity.ok(building.toDTO())
    }

    @GetMapping("/search")
    fun searchBuildings(@RequestParam query: String): ResponseEntity<List<BuildingDTO>> {
        // Track search metric
        recordMetric("/api/buildings/search")
        
        val buildings = buildingService.searchBuildings(query)
        return ResponseEntity.ok(buildings.map { it.toDTO() })
    }
    
    @GetMapping("/category/{category}")
    fun findBuildingsByCategory(@PathVariable category: String): ResponseEntity<List<BuildingDTO>> {
        // Track metric with category tag
        recordMetric("/api/buildings/category/${category}", category = category)
        
        val buildings = buildingService.findBuildingsByCategory(category)
        return ResponseEntity.ok(buildings.map { it.toDTO() })
    }
    
    @GetMapping("/nearby")
    fun findNearbyBuildings(
        @RequestParam userLat: Double,
        @RequestParam userLon: Double,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<BuildingDTO>> {
        // Track nearby query metric with start time
        val startTime = System.currentTimeMillis()
        
        val buildings = buildingService.findBuildingsOrderedByDistance(userLat, userLon, page, size)
        
        // Track response time for nearby query
        val responseTime = System.currentTimeMillis() - startTime
        recordMetric("/api/buildings/nearby", responseTimeMs = responseTime)
        
        return ResponseEntity.ok(buildings.map { it.toDTO() })
    }

    @PostMapping
    fun createBuilding(@RequestBody building: Building): ResponseEntity<BuildingDTO> {
        // Track metric
        recordMetric("/api/buildings", isPost = true)
        
        val createdBuilding = buildingService.createBuilding(building)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBuilding.toDTO())
    }

    @PutMapping("/{id}")
    fun updateBuilding(
        @PathVariable id: Long, 
        @RequestBody building: Building
    ): ResponseEntity<BuildingDTO> {
        // Track metric with building ID
        recordMetric("/api/buildings/${id}", buildingId = id, isPut = true)
        
        val updatedBuilding = buildingService.updateBuilding(id, building)
        return ResponseEntity.ok(updatedBuilding.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteBuilding(@PathVariable id: Long): ResponseEntity<Void> {
        // Track metric with building ID
        recordMetric("/api/buildings/${id}", buildingId = id, isDelete = true)
        
        buildingService.deleteBuilding(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/{buildingId}/places")
    fun getPlacesByBuildingId(@PathVariable buildingId: Long): ResponseEntity<List<PlaceDTO>> {
        // Track metric with building ID
        recordMetric("/api/buildings/${buildingId}/places", buildingId = buildingId)
        
        val places = buildingService.getPlacesByBuildingId(buildingId)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }
    
    @PostMapping("/{buildingId}/places")
    fun addPlaceToBuilding(
        @PathVariable buildingId: Long, 
        @RequestBody place: Place
    ): ResponseEntity<PlaceDTO> {
        // Track metric with building ID
        recordMetric("/api/buildings/${buildingId}/places", buildingId = buildingId, isPost = true)
        
        val createdPlace = buildingService.addPlaceToBuilding(buildingId, place)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace.toDTO())
    }
    
    // Helper method to record metrics with appropriate tags
    private fun recordMetric(
        endpoint: String,
        buildingId: Long? = null,
        category: String? = null,
        isPost: Boolean = false,
        isPut: Boolean = false,
        isDelete: Boolean = false,
        responseTimeMs: Long? = null
    ) {
        // Create tags list
        val tags = mutableListOf(
            Tag.of("endpoint", endpoint)
        )
        
        // Add optional tags
        buildingId?.let { tags.add(Tag.of("building_id", it.toString())) }
        category?.let { tags.add(Tag.of("category", it)) }
        
        // Add HTTP method tag
        val method = when {
            isPost -> "POST"
            isPut -> "PUT"
            isDelete -> "DELETE"
            else -> "GET"
        }
        tags.add(Tag.of("method", method))
        
        // Increment request counter
        meterRegistry.counter("explorandes_backend.api.request.count", tags).increment()
        
        // Record response time if provided
        responseTimeMs?.let {
            meterRegistry.timer("explorandes_backend.api.response.time", tags).record(it, java.util.concurrent.TimeUnit.MILLISECONDS)
        }
        
        // Try to catch any errors and record them
        try {
            // This is called at the beginning of the method, so we can't catch all errors
            // For comprehensive error tracking, consider using AOP or exception handlers
        } catch (e: Exception) {
            meterRegistry.counter("explorandes_backend.api.errors", tags).increment()
        }
    }
}