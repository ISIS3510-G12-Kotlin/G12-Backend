package com.example.demo.service

import com.example.demo.model.Event
import com.example.demo.repository.BuildingRepository
import com.example.demo.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import com.example.demo.dto.response.EventDTO


@Service
class EventService(
    private val eventRepository: EventRepository,
    private val buildingRepository: BuildingRepository
) {
    fun getAllEvents(): List<Event> = eventRepository.findAll()
    
    fun getEventById(id: Long): Event = eventRepository.findById(id)
        .orElseThrow { NoSuchElementException("Event not found with id: $id") }
    
    fun getEventsByType(type: String): List<Event> = eventRepository.findByType(type)
    
    fun getEventsByLocation(locationId: Long): List<Event> = eventRepository.findByLocationId(locationId)
    
    fun getEventsByTimeRange(start: LocalDateTime, end: LocalDateTime): List<Event> = 
        eventRepository.findByTimeRange(start, end)
    
    fun getUpcomingEvents(limit: Int): List<Event> {
        val now = LocalDateTime.now()
        val events = eventRepository.findUpcomingEvents(now)
        return if (limit > 0) events.take(limit) else events
    }
    
    fun searchEvents(query: String): List<Event> = eventRepository.searchEvents(query)
    
    @Transactional
    fun createEvent(event: Event): Event {
        // If location ID is provided, associate with the building
        event.location?.id?.let { locationId ->
            val building = buildingRepository.findByIdOrNull(locationId)
            event.location = building
        }
        
        return eventRepository.save(event)
    }
    
    @Transactional
    fun updateEvent(id: Long, updatedEvent: Event): Event {
        val existingEvent = getEventById(id)
        
        // Update fields
        existingEvent.title = updatedEvent.title
        existingEvent.description = updatedEvent.description
        existingEvent.imageUrl = updatedEvent.imageUrl
        existingEvent.type = updatedEvent.type
        existingEvent.startTime = updatedEvent.startTime
        existingEvent.endTime = updatedEvent.endTime
        existingEvent.updatedAt = LocalDateTime.now()
        
        // Update location if provided
        updatedEvent.location?.id?.let { locationId ->
            val building = buildingRepository.findByIdOrNull(locationId)
            existingEvent.location = building
        }
        
        return eventRepository.save(existingEvent)
    }
    
    @Transactional
    fun deleteEvent(id: Long) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Event not found with id: $id")
        }
    }
    
    // Helper method to load events data from JSON
    @Transactional
    fun loadEventsFromDTO(eventDTOs: List<EventDTO>): List<Event> {
        val events = eventDTOs.map { dto ->
            val event = Event(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                type = dto.type,
                startTime = LocalDateTime.parse(dto.startTime),
                endTime = LocalDateTime.parse(dto.endTime),
                location = null,
                createdAt = LocalDateTime.now(), // O usa dto.createdAt si lo agregas
                updatedAt = LocalDateTime.now()
            )

            // Asociar con building si hay locationId
            dto.locationId?.let { locationId ->
                val building = buildingRepository.findByIdOrNull(locationId)
                event.location = building
            }

            event
        }

        return eventRepository.saveAll(events)
    }

}