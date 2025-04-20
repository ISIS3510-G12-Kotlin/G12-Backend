package com.example.demo.controllers

import com.example.demo.dto.response.EventDTO
import com.example.demo.mapper.toDTO
import com.example.demo.model.Event
import com.example.demo.service.EventService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/events")
class EventController(private val eventService: EventService) {

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<EventDTO>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): ResponseEntity<EventDTO> {
        val event = eventService.getEventById(id)
        return ResponseEntity.ok(event.toDTO())
    }

    @GetMapping("/type/{type}")
    fun getEventsByType(@PathVariable type: String): ResponseEntity<List<EventDTO>> {
        val events = eventService.getEventsByType(type)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/location/{locationId}")
    fun getEventsByLocation(@PathVariable locationId: Long): ResponseEntity<List<EventDTO>> {
        val events = eventService.getEventsByLocation(locationId)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/timeRange")
    fun getEventsByTimeRange(
        @RequestParam start: String,
        @RequestParam end: String
    ): ResponseEntity<List<EventDTO>> {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val startDateTime = LocalDateTime.parse(start, formatter)
        val endDateTime = LocalDateTime.parse(end, formatter)
        
        val events = eventService.getEventsByTimeRange(startDateTime, endDateTime)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/upcoming")
    fun getUpcomingEvents(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<List<EventDTO>> {
        val events = eventService.getUpcomingEvents(limit)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/search")
    fun searchEvents(@RequestParam query: String): ResponseEntity<List<EventDTO>> {
        val events = eventService.searchEvents(query)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @PostMapping
    fun createEvent(@RequestBody event: Event): ResponseEntity<EventDTO> {
        val createdEvent = eventService.createEvent(event)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent.toDTO())
    }

    @PutMapping("/{id}")
    fun updateEvent(
        @PathVariable id: Long, 
        @RequestBody event: Event
    ): ResponseEntity<EventDTO> {
        val updatedEvent = eventService.updateEvent(id, event)
        return ResponseEntity.ok(updatedEvent.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<Void> {
        eventService.deleteEvent(id)
        return ResponseEntity.noContent().build()
    }
}