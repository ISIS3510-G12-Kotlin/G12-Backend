package com.example.demo.controllers

import com.example.demo.dto.response.EventDTO
import com.example.demo.mapper.toDTO
import com.example.demo.model.Event
import com.example.demo.service.EventService
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
    private val meterRegistry: MeterRegistry // Inject MeterRegistry
) {

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<EventDTO>> {
        // Track metric
        recordMetric("/api/events")
        
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): ResponseEntity<EventDTO> {
        // Track metric with event ID
        recordMetric("/api/events/${id}", eventId = id)
        
        val event = eventService.getEventById(id)
        return ResponseEntity.ok(event.toDTO())
    }

    @GetMapping("/type/{type}")
    fun getEventsByType(@PathVariable type: String): ResponseEntity<List<EventDTO>> {
        // Track metric with event type
        recordMetric("/api/events/type/${type}", type = type)
        
        val events = eventService.getEventsByType(type)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/location/{locationId}")
    fun getEventsByLocation(@PathVariable locationId: Long): ResponseEntity<List<EventDTO>> {
        // Track metric with location ID
        recordMetric("/api/events/location/${locationId}", locationId = locationId)
        
        val events = eventService.getEventsByLocation(locationId)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/timeRange")
    fun getEventsByTimeRange(
        @RequestParam start: String,
        @RequestParam end: String
    ): ResponseEntity<List<EventDTO>> {
        // Track metric 
        recordMetric("/api/events/timeRange")
        
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val startDateTime = LocalDateTime.parse(start, formatter)
        val endDateTime = LocalDateTime.parse(end, formatter)
        
        val events = eventService.getEventsByTimeRange(startDateTime, endDateTime)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/upcoming")
    fun getUpcomingEvents(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<List<EventDTO>> {
        // Track metric
        recordMetric("/api/events/upcoming")
        
        val events = eventService.getUpcomingEvents(limit)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @GetMapping("/search")
    fun searchEvents(@RequestParam query: String): ResponseEntity<List<EventDTO>> {
        // Track metric
        recordMetric("/api/events/search")
        
        val events = eventService.searchEvents(query)
        return ResponseEntity.ok(events.map { it.toDTO() })
    }

    @PostMapping
    fun createEvent(@RequestBody event: Event): ResponseEntity<EventDTO> {
        // Track metric with event type if available
        recordMetric("/api/events", type = event.type, isPost = true)
        
        val createdEvent = eventService.createEvent(event)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent.toDTO())
    }

    @PutMapping("/{id}")
    fun updateEvent(
        @PathVariable id: Long, 
        @RequestBody event: Event
    ): ResponseEntity<EventDTO> {
        // Track metric with event ID and type
        recordMetric("/api/events/${id}", eventId = id, type = event.type, isPut = true)
        
        val updatedEvent = eventService.updateEvent(id, event)
        return ResponseEntity.ok(updatedEvent.toDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<Void> {
        // Track metric with event ID
        recordMetric("/api/events/${id}", eventId = id, isDelete = true)
        
        eventService.deleteEvent(id)
        return ResponseEntity.noContent().build()
    }
    
    // Helper method to record metrics with appropriate tags
    private fun recordMetric(
        endpoint: String,
        eventId: Long? = null,
        type: String? = null,
        locationId: Long? = null,
        isPost: Boolean = false,
        isPut: Boolean = false,
        isDelete: Boolean = false,
        responseTimeMs: Long? = null
    ) {
        // Create tags list
        val tags = mutableListOf(
            Tag.of("endpoint", endpoint)
        )
        
        // Add optional tags
        eventId?.let { tags.add(Tag.of("event_id", it.toString())) }
        type?.let { tags.add(Tag.of("type", it)) }
        locationId?.let { tags.add(Tag.of("location_id", it.toString())) }
        
        // Add HTTP method tag
        val method = when {
            isPost -> "POST"
            isPut -> "PUT"
            isDelete -> "DELETE"
            else -> "GET"
        }
        tags.add(Tag.of("method", method))
        
        // Increment request counter
        meterRegistry.counter("explorandes_backend.api.request.count", tags).increment()
        
        // Record response time if provided
        responseTimeMs?.let {
            meterRegistry.timer("explorandes_backend.api.response.time", tags).record(it, java.util.concurrent.TimeUnit.MILLISECONDS)
        }
        
        // Try to catch any errors and record them
        try {
            // This is called at the beginning of the method, so we can't catch all errors
            // For comprehensive error tracking, consider using AOP or exception handlers
        } catch (e: Exception) {
            meterRegistry.counter("explorandes_backend.api.errors", tags).increment()
        }
    }
}