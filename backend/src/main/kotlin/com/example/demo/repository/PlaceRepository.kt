package com.example.demo.repository

import com.example.demo.model.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : JpaRepository<Place, Long> {
    fun findByBuildingId(buildingId: Long): List<Place>
    
    fun findByCategory(category: String): List<Place>
    
    fun findByFloorContaining(floorCode: String): List<Place>
    
    @Query("SELECT p FROM Place p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchPlaces(@Param("query") query: String): List<Place>
}