package com.example.demo.repository

import com.example.demo.model.Floor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FloorRepository : JpaRepository<Floor, Long> {
    fun findByBuildingIdOrderByNumberAsc(buildingId: Long): List<Floor>
}