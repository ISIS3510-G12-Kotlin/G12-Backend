package com.example.demo.mapper

import com.example.demo.dto.response.EventDTO
import com.example.demo.model.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Extension function to convert Event to EventDTO
fun Event.toDTO(): EventDTO {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    
    return EventDTO(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        type = this.type,
        startTime = this.startTime.format(formatter),
        endTime = this.endTime.format(formatter),
        locationId = this.location?.id,
        locationName = this.location?.name
    )
}

// Extension function to parse string dates into LocalDateTime
fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
}