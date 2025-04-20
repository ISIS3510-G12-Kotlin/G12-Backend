// File: com/example/demo/controllers/PlaceController.kt
package com.example.demo.controllers

import com.example.demo.dto.response.PlaceDTO
import com.example.demo.mapper.toDTO
import com.example.demo.model.Place
import com.example.demo.service.PlaceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/places")
class PlaceController(private val placeService: PlaceService) {

    @GetMapping
    fun getAllPlaces(): ResponseEntity<List<PlaceDTO>> {
        val places = placeService.getAllPlaces()
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getPlaceById(@PathVariable id: Long): ResponseEntity<PlaceDTO> {
        val place = placeService.getPlaceById(id)
        return ResponseEntity.ok(place.toDTO())
    }

    @GetMapping("/building/{buildingId}")
    fun getPlacesByBuilding(@PathVariable buildingId: Long): ResponseEntity<List<PlaceDTO>> {
        val places = placeService.getPlacesByBuilding(buildingId)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/category/{category}")
    fun getPlacesByCategory(@PathVariable category: String): ResponseEntity<List<PlaceDTO>> {
        val places = placeService.getPlacesByCategory(category)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }
    
    @GetMapping("/floor/{floorCode}")
    fun getPlacesByFloor(@PathVariable floorCode: String): ResponseEntity<List<PlaceDTO>> {
        val places = placeService.getPlacesByFloor(floorCode)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @GetMapping("/search")
    fun searchPlaces(@RequestParam query: String): ResponseEntity<List<PlaceDTO>> {
        val places = placeService.searchPlaces(query)
        return ResponseEntity.ok(places.map { it.toDTO() })
    }

    @PostMapping
    fun createPlace(@RequestBody place: Place): ResponseEntity<PlaceDTO> {
        val createdPlace = placeService.createPlace(place)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace.toDTO())
    }

    @PutMapping("/{id}")
    fun updatePlace(
        @PathVariable id: Long, 
        @RequestBody place: Place
    ): ResponseEntity<PlaceDTO> {
        val updatedPlace = placeService.updatePlace(id, place)
        return ResponseEntity.ok(updatedPlace.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deletePlace(@PathVariable id: Long): ResponseEntity<Void> {
        placeService.deletePlace(id)
        return ResponseEntity.noContent().build()
    }
}