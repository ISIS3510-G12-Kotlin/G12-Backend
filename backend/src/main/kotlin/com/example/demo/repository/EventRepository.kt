package com.example.demo.repository

import com.example.demo.model.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepository : JpaRepository<Event, Long> {
    fun findByType(type: String): List<Event>
    
    fun findByLocationId(locationId: Long): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.startTime >= :start AND e.endTime <= :end")
    fun findByTimeRange(start: LocalDateTime, end: LocalDateTime): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.startTime >= :now ORDER BY e.startTime ASC")
    fun findUpcomingEvents(now: LocalDateTime): List<Event>
    
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchEvents(query: String): List<Event>
}