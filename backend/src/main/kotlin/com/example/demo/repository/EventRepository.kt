package com.example.demo.repository

import com.example.demo.model.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepository : JpaRepository<Event, Long> {
    // Add this new method to fetch all events with locations in one query
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location")
    fun findAllWithLocations(): List<Event>
    
    // Update existing methods to use JOIN FETCH
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location WHERE e.type = :type")
    fun findByType(@Param("type") type: String): List<Event>
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location WHERE e.location.id = :locationId")
    fun findByLocationId(@Param("locationId") locationId: Long): List<Event>
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location WHERE e.startTime >= :start AND e.endTime <= :end")
    fun findByTimeRange(@Param("start") start: LocalDateTime, @Param("end") end: LocalDateTime): List<Event>
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location WHERE e.startTime >= :now ORDER BY e.startTime ASC")
    fun findUpcomingEvents(@Param("now") now: LocalDateTime): List<Event>
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.location WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchEvents(@Param("query") query: String): List<Event>
}