package com.example.demo.repository

import com.example.demo.model.Building
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import org.springframework.data.domain.Pageable

@Repository
interface BuildingRepository : JpaRepository<Building, Long> {
    fun findByCode(code: String): Optional<Building>
    
    @Query("SELECT b FROM Building b WHERE " + 
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " + 
           "LOWER(b.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchBuildings(@Param("query") query: String): List<Building>
    
    @Query("SELECT b FROM Building b WHERE b.category = :category")
    fun findByCategory(@Param("category") category: String): List<Building>
    
    @Query("SELECT b FROM Building b ORDER BY " + 
           "SQRT(POWER(:userLat - b.latitude, 2) + POWER(:userLon - b.longitude, 2))")
    fun findAllOrderedByDistance(
        @Param("userLat") userLat: Double, 
        @Param("userLon") userLon: Double,
        pageable: Pageable
    ): List<Building>
}