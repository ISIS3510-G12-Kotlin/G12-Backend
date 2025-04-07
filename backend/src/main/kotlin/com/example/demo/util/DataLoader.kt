package com.example.demo.util

import com.example.demo.model.*
import com.example.demo.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

/**
 * This class loads initial data into the database when the application starts.
 * Only runs when the "init-data" profile is active.
 * To use: Run the application with -Dspring.profiles.active=init-data
 */
@Configuration
@Profile("init-data")
class DataLoader(
    private val passwordEncoder: PasswordEncoder
) {
    @Bean
    fun initDatabase(
        buildingRepository: BuildingRepository,
        campusRepository: CampusRepository,
        floorRepository: FloorRepository,
        roomRepository: RoomRepository,
        placeRepository: PlaceRepository,
        userRepository: UserRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            // Only load data if the database is empty
            if (buildingRepository.count() == 0L) {
                println("Initializing database with sample data...")
                
                // Create campus
                val mainCampus = Campus(
                    name = "Main Campus",
                    description = "The main university campus located in the city center"
                )
                
                // Rest of your existing code...
                
                // Create users based on your actual User model properties
                val users = listOf(
                    User(username = "admin", email = "admin@example.com", password = passwordEncoder.encode("password123"), firstName = "Admin", lastName = "User"),
                    User(username = "student1", email = "student1@example.com", password = passwordEncoder.encode("password123"), firstName = "Student", lastName = "One"),
                    User(username = "student2", email = "student2@example.com", password = passwordEncoder.encode("password123"), firstName = "Student", lastName = "Two"),
                    User(username = "professor", email = "professor@example.com", password = passwordEncoder.encode("password123"), firstName = "Professor", lastName = "Example"),
                    User(username = "librarian", email = "librarian@example.com", password = passwordEncoder.encode("password123"), firstName = "Library", lastName = "Staff"),
                    User(username = "staff", email = "staff@example.com", password = passwordEncoder.encode("password123"), firstName = "Staff", lastName = "Member"),
                    User(username = "guest", email = "guest@example.com", password = passwordEncoder.encode("password123"), firstName = "Guest", lastName = "User"),
                    User(username = "researcher", email = "researcher@example.com", password = passwordEncoder.encode("password123"), firstName = "Research", lastName = "Staff"),
                    User(username = "technician", email = "technician@example.com", password = passwordEncoder.encode("password123"), firstName = "Technical", lastName = "Support"),
                    User(username = "visitor", email = "visitor@example.com", password = passwordEncoder.encode("password123"), firstName = "Campus", lastName = "Visitor")
                )


                userRepository.saveAll(users)
                println("Users data loaded.")
                println("Sample data initialization complete!")
            } else {
                println("Database already contains data, skipping initialization.")
            }
        }
    }
}