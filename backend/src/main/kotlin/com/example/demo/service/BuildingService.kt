package com.example.demo.service

import com.example.demo.model.Building
import com.example.demo.model.Floor
import com.example.demo.model.Room
import com.example.demo.repository.BuildingRepository
import com.example.demo.repository.FloorRepository
import com.example.demo.repository.RoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BuildingService(
    private val buildingRepository: BuildingRepository,
    private val floorRepository: FloorRepository,
    private val roomRepository: RoomRepository
) {
    @Transactional(readOnly = true)
    fun findAllBuildings(): List<Building> = buildingRepository.findAll()

    @Transactional(readOnly = true)
    fun findBuildingById(id: Long): Building = 
        buildingRepository.findById(id)
            .orElseThrow { NoSuchElementException("Building not found with id: $id") }

    @Transactional(readOnly = true)
    fun findBuildingByCode(code: String): Building = 
        buildingRepository.findByCode(code)
            .orElseThrow { NoSuchElementException("Building not found with code: $code") }

    @Transactional(readOnly = true)
    fun searchBuildings(query: String): List<Building> = 
        buildingRepository.searchBuildings(query)

    @Transactional(readOnly = true)
    fun findBuildingsByCampus(campusId: Long): List<Building> = 
        buildingRepository.findByCampusId(campusId)

    @Transactional
    fun createBuilding(building: Building): Building = 
        buildingRepository.save(building)

    @Transactional
    fun updateBuilding(id: Long, building: Building): Building {
        val existingBuilding = findBuildingById(id)
        existingBuilding.name = building.name
        existingBuilding.code = building.code
        existingBuilding.description = building.description
        existingBuilding.latitude = building.latitude
        existingBuilding.longitude = building.longitude
        existingBuilding.imageUrl = building.imageUrl
        existingBuilding.campus = building.campus
        
        return buildingRepository.save(existingBuilding)
    }

    @Transactional
    fun deleteBuilding(id: Long) {
        if (buildingRepository.existsById(id)) {
            buildingRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Building not found with id: $id")
        }
    }

    @Transactional(readOnly = true)
    fun getFloorsByBuildingId(buildingId: Long): List<Floor> = 
        floorRepository.findByBuildingIdOrderByNumberAsc(buildingId)

    @Transactional
    fun addFloorToBuilding(buildingId: Long, floor: Floor): Floor {
        val building = findBuildingById(buildingId)
        floor.building = building
        return floorRepository.save(floor)
    }

    @Transactional(readOnly = true)
    fun getRoomsByFloorId(floorId: Long): List<Room> = 
        roomRepository.findByFloorId(floorId)

    @Transactional(readOnly = true)
    fun getRoomsByBuildingId(buildingId: Long): List<Room> = 
        roomRepository.findByBuildingId(buildingId)

    @Transactional
    fun addRoomToFloor(floorId: Long, room: Room): Room {
        val floor = floorRepository.findById(floorId)
            .orElseThrow { NoSuchElementException("Floor not found with id: $floorId") }
        room.floor = floor
        return roomRepository.save(room)
    }
}