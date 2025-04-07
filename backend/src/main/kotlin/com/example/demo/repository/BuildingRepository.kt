package com.example.demo.repository

import com.example.demo.model.Building
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BuildingRepository : JpaRepository<Building, Long> {
    fun findByCode(code: String): Optional<Building>
    
    @Query("SELECT b FROM Building b WHERE " + 
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " + 
           "LOWER(b.code) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchBuildings(@Param("query") query: String): List<Building>
    
    @Query("SELECT b FROM Building b JOIN b.campus c WHERE c.id = :campusId")
    fun findByCampusId(@Param("campusId") campusId: Long): List<Building>
}