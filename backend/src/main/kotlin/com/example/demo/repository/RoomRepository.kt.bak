package com.example.demo.repository

import com.example.demo.model.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : JpaRepository<Room, Long> {
    fun findByFloorId(floorId: Long): List<Room>
    
    @Query("SELECT r FROM Room r JOIN r.floor f JOIN f.building b WHERE b.id = :buildingId")
    fun findByBuildingId(@Param("buildingId") buildingId: Long): List<Room>
}