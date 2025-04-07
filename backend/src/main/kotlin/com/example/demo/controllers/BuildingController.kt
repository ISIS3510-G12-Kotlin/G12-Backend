package com.example.demo.controllers

import com.example.demo.model.Building
import com.example.demo.model.Floor
import com.example.demo.model.Room
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

    @GetMapping("/campus/{campusId}")
    fun findBuildingsByCampus(@PathVariable campusId: Long): ResponseEntity<List<Building>> {
        val buildings = buildingService.findBuildingsByCampus(campusId)
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

    @GetMapping("/{buildingId}/floors")
    fun getFloorsByBuildingId(@PathVariable buildingId: Long): ResponseEntity<List<Floor>> {
        val floors = buildingService.getFloorsByBuildingId(buildingId)
        return ResponseEntity.ok(floors)
    }

    @PostMapping("/{buildingId}/floors")
    fun addFloorToBuilding(
        @PathVariable buildingId: Long, 
        @RequestBody floor: Floor
    ): ResponseEntity<Floor> {
        val createdFloor = buildingService.addFloorToBuilding(buildingId, floor)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFloor)
    }

    @GetMapping("/floors/{floorId}/rooms")
    fun getRoomsByFloorId(@PathVariable floorId: Long): ResponseEntity<List<Room>> {
        val rooms = buildingService.getRoomsByFloorId(floorId)
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/{buildingId}/rooms")
    fun getRoomsByBuildingId(@PathVariable buildingId: Long): ResponseEntity<List<Room>> {
        val rooms = buildingService.getRoomsByBuildingId(buildingId)
        return ResponseEntity.ok(rooms)
    }

    @PostMapping("/floors/{floorId}/rooms")
    fun addRoomToFloor(
        @PathVariable floorId: Long, 
        @RequestBody room: Room
    ): ResponseEntity<Room> {
        val createdRoom = buildingService.addRoomToFloor(floorId, room)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom)
    }
}