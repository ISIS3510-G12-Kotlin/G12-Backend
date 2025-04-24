package com.example.demo.util

import com.example.demo.model.*
import com.example.demo.repository.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.core.io.ClassPathResource
import java.time.LocalDateTime
import com.example.demo.dto.response.EventDTO

@Configuration
@Profile("init-data")
class DataLoader(
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun initDatabase(
        buildingRepository: BuildingRepository,
        placeRepository: PlaceRepository,
        userRepository: UserRepository,
        eventRepository: EventRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            println("Clearing previous data...")
            eventRepository.deleteAll()

            if (buildingRepository.count() == 0L) {
                println("Initializing database with sample data...")

                val locationsJson = ClassPathResource("data/locations.json").inputStream.readBytes().toString(Charsets.UTF_8)
                val locations: List<LocationDTO> = objectMapper.readValue(locationsJson)

                val buildingsMap = mutableMapOf<Long, Building>()
                val buildings = locations.map { locationDTO ->
                    val building = Building(
                        name = locationDTO.name,
                        code = locationDTO.name.substring(0, 1) + locationDTO.name.substring(locationDTO.name.indexOf(" ") + 1),
                        description = locationDTO.description ?: "",
                        latitude = locationDTO.latitude,
                        longitude = locationDTO.longitude,
                        imageUrl = locationDTO.image_url,
                        category = locationDTO.category
                    )
                    buildingsMap[locationDTO.location_id] = building
                    building
                }

                buildingRepository.saveAll(buildings)
                println("Buildings data loaded from Supabase format.")

                val placesJson = ClassPathResource("data/places.json").inputStream.readBytes().toString(Charsets.UTF_8)
                val placeDTOs: List<PlaceDTO> = objectMapper.readValue(placesJson)

                val places = placeDTOs.mapNotNull { placeDTO ->
                    val building = buildingsMap[placeDTO.id_location] ?: return@mapNotNull null

                    Place(
                        name = placeDTO.name,
                        code = building.code + "-" + placeDTO.floor?.replace("_", ""),
                        category = when {
                            placeDTO.name.contains("Puente") -> "Connection"
                            placeDTO.name.contains("Lab") -> "Laboratory"
                            placeDTO.name.contains("Cancha") || placeDTO.name.contains("Piscina") -> "Sports"
                            placeDTO.name.contains("Terraza") -> "Common Area"
                            placeDTO.name.contains("Sala") -> "Study Area"
                            else -> "Other"
                        },
                        floor = placeDTO.floor,
                        coordinates = "${building.latitude},${building.longitude}",
                        imageUrl = placeDTO.url_image,
                        building = building
                    )
                }

                placeRepository.saveAll(places)
                println("Places data loaded from Supabase format.")

                val users = listOf(
                    User(
                        username = "admin",
                        email = "admin@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Admin",
                        lastName = "User"
                    ),
                    User(
                        username = "student1",
                        email = "student1@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Student",
                        lastName = "One"
                    ),
                    User(
                        username = "student2",
                        email = "student2@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Student",
                        lastName = "Two"
                    ),
                    User(
                        username = "professor",
                        email = "professor@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Professor",
                        lastName = "Example"
                    ),
                    User(
                        username = "librarian",
                        email = "librarian@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Library",
                        lastName = "Staff"
                    ),
                    User(
                        username = "staff",
                        email = "staff@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Staff",
                        lastName = "Member"
                    ),
                    User(
                        username = "guest",
                        email = "guest@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Guest",
                        lastName = "User"
                    ),
                    User(
                        username = "researcher",
                        email = "researcher@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Research",
                        lastName = "Staff"
                    ),
                    User(
                        username = "technician",
                        email = "technician@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Technical",
                        lastName = "Support"
                    ),
                    User(
                        username = "visitor",
                        email = "visitor@example.com",
                        password = passwordEncoder.encode("password123"),
                        firstName = "Campus",
                        lastName = "Visitor"
                    )
                )

                userRepository.saveAll(users)
                println("Users data loaded.")
            }

            try {
                val eventsJson = ClassPathResource("data/events.json").inputStream.readBytes().toString(Charsets.UTF_8)
                val eventDTOs: List<EventDTO> = objectMapper.readValue(eventsJson)

                val events = eventDTOs.map { eventDTO ->
                    val building = eventDTO.locationId?.let { id -> buildingRepository.findById(id).orElse(null) }
                    Event(
                        id = 0,
                        title = eventDTO.title,
                        description = eventDTO.description ?: "",
                        imageUrl = eventDTO.imageUrl,
                        type = eventDTO.type ?: "event",
                        startTime = LocalDateTime.parse(eventDTO.startTime),
                        endTime = LocalDateTime.parse(eventDTO.endTime),
                        location = building,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                }

                eventRepository.saveAll(events)
                println("Events data loaded.")
            } catch (e: Exception) {
                println("Error loading events data: ${e.message}")
                e.printStackTrace()
            }

            println("Sample data initialization complete!")
        }
    }
}
