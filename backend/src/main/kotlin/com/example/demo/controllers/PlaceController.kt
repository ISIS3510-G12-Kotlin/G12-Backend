// File: com/example/demo/controllers/PlaceController.kt
package com.example.demo.controllers

import com.example.demo.dto.response.PlaceDTO
import com.example.demo.mapper.toDTO
import com.example.demo.model.Place
import com.example.demo.service.PlaceService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/places")
class PlaceController(
    private val placeService: PlaceService,
    private val meterRegistry: MeterRegistry // Inject MeterRegistry
) {

    @GetMapping
    fun getAllPlaces(): ResponseEntity<List<PlaceDTO>> {
        // Track metric
        recordMetric("/api/places")
        
        val places = placeService.getAllPlaces()
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getPlaceById(@PathVariable id: Long): ResponseEntity<PlaceDTO> {
        // Track metric with place ID
        recordMetric("/api/places/${id}", placeId = id)
        
        val place = placeService.getPlaceById(id)
        return ResponseEntity.ok(place.toDTO())
    }

    @GetMapping("/building/{buildingId}")
    fun getPlacesByBuilding(@PathVariable buildingId: Long): ResponseEntity<List<PlaceDTO>> {
        // Track metric with building ID
        recordMetric("/api/places/building/${buildingId}", buildingId = buildingId)
        
        val places = placeService.getPlacesByBuilding(buildingId)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/category/{category}")
    fun getPlacesByCategory(@PathVariable category: String): ResponseEntity<List<PlaceDTO>> {
        // Track metric with category
        recordMetric("/api/places/category/${category}", category = category)
        
        val places = placeService.getPlacesByCategory(category)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }
    
    @GetMapping("/floor/{floorCode}")
    fun getPlacesByFloor(@PathVariable floorCode: String): ResponseEntity<List<PlaceDTO>> {
        // Track metric with floor code
        recordMetric("/api/places/floor/${floorCode}", floor = floorCode)
        
        val places = placeService.getPlacesByFloor(floorCode)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/search")
    fun searchPlaces(@RequestParam query: String): ResponseEntity<List<PlaceDTO>> {
        // Track metric
        recordMetric("/api/places/search")
        
        val places = placeService.searchPlaces(query)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @PostMapping
    fun createPlace(@RequestBody place: Place): ResponseEntity<PlaceDTO> {
        // Track metric with category if available
        recordMetric("/api/places", category = place.category, isPost = true)
        
        val createdPlace = placeService.createPlace(place)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace.toDTO())
    }

    @PutMapping("/{id}")
    fun updatePlace(
        @PathVariable id: Long, 
        @RequestBody place: Place
    ): ResponseEntity<PlaceDTO> {
        // Track metric with place ID and category
        recordMetric("/api/places/${id}", placeId = id, category = place.category, isPut = true)
        
        val updatedPlace = placeService.updatePlace(id, place)
        return ResponseEntity.ok(updatedPlace.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deletePlace(@PathVariable id: Long): ResponseEntity<Void> {
        // Track metric with place ID
        recordMetric("/api/places/${id}", placeId = id, isDelete = true)
        
        placeService.deletePlace(id)
        return ResponseEntity.noContent().build()
    }
    
    // Helper method to record metrics with appropriate tags
    private fun recordMetric(
        endpoint: String,
        placeId: Long? = null,
        buildingId: Long? = null,
        category: String? = null,
        floor: String? = null,
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
        placeId?.let { tags.add(Tag.of("place_id", it.toString())) }
        buildingId?.let { tags.add(Tag.of("building_id", it.toString())) }
        category?.let { tags.add(Tag.of("category", it)) }
        floor?.let { tags.add(Tag.of("floor", it)) }
        
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