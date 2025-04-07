package com.example.demo.controllers

import com.example.demo.model.Place
import com.example.demo.service.PlaceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/places")
class PlaceController(private val placeService: PlaceService) {

    @GetMapping
    fun getAllPlaces(): ResponseEntity<List<Place>> {
        val places = placeService.getAllPlaces()
        return ResponseEntity.ok(places)
    }

    @GetMapping("/{id}")
    fun getPlaceById(@PathVariable id: Long): ResponseEntity<Place> {
        val place = placeService.getPlaceById(id)
        return ResponseEntity.ok(place)
    }

    @GetMapping("/building/{buildingId}")
    fun getPlacesByBuilding(@PathVariable buildingId: Long): ResponseEntity<List<Place>> {
        val places = placeService.getPlacesByBuilding(buildingId)
        return ResponseEntity.ok(places)
    }

    @GetMapping("/category/{category}")
    fun getPlacesByCategory(@PathVariable category: String): ResponseEntity<List<Place>> {
        val places = placeService.getPlacesByCategory(category)
        return ResponseEntity.ok(places)
    }

    @GetMapping("/search")
    fun searchPlaces(@RequestParam query: String): ResponseEntity<List<Place>> {
        val places = placeService.searchPlaces(query)
        return ResponseEntity.ok(places)
    }

    @PostMapping
    fun createPlace(@RequestBody place: Place): ResponseEntity<Place> {
        val createdPlace = placeService.createPlace(place)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace)
    }

    @PutMapping("/{id}")
    fun updatePlace(
        @PathVariable id: Long, 
        @RequestBody place: Place
    ): ResponseEntity<Place> {
        val updatedPlace = placeService.updatePlace(id, place)
        return ResponseEntity.ok(updatedPlace)
    }

    @DeleteMapping("/{id}")
    fun deletePlace(@PathVariable id: Long): ResponseEntity<Void> {
        placeService.deletePlace(id)
        return ResponseEntity.noContent().build()
    }
}