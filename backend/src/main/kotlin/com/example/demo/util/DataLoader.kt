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
                
                val northCampus = Campus(
                    name = "North Campus",
                    description = "The northern extension of the university"
                )
                
                campusRepository.saveAll(listOf(mainCampus, northCampus))
                println("Campus data loaded.")
                
                // Create buildings
                val marioLaserna = Building(
                    name = "Mario Laserna",
                    code = "ML",
                    description = "Engineering Building with classrooms and labs for engineering students",
                    latitude = 4.602442,
                    longitude = -74.065952,
                    imageUrl = "https://example.com/images/ml.jpg",
                    campus = mainCampus
                )
                
                val wBuilding = Building(
                    name = "W Building",
                    code = "W",
                    description = "Science and admin building with labs and offices",
                    latitude = 4.603018,
                    longitude = -74.066217,
                    imageUrl = "https://example.com/images/w.jpg",
                    campus = mainCampus
                )
                
                val santoDomingo = Building(
                    name = "Santo Domingo",
                    code = "SD",
                    description = "Main library and study center",
                    latitude = 4.602780,
                    longitude = -74.065553,
                    imageUrl = "https://example.com/images/sd.jpg",
                    campus = mainCampus
                )
                
                val sportsCenter = Building(
                    name = "Sports Center",
                    code = "Z",
                    description = "Sports facilities and gym",
                    latitude = 4.601982,
                    longitude = -74.064964,
                    imageUrl = "https://example.com/images/z.jpg",
                    campus = northCampus
                )
                
                buildingRepository.saveAll(listOf(marioLaserna, wBuilding, santoDomingo, sportsCenter))
                println("Buildings data loaded.")
                
                // Create floors for Mario Laserna
                val mlFloor1 = Floor(
                    number = 1,
                    name = "Ground Floor",
                    description = "Main entrance and labs",
                    building = marioLaserna
                )
                
                val mlFloor2 = Floor(
                    number = 2,
                    name = "Second Floor",
                    description = "Classrooms and offices",
                    building = marioLaserna
                )
                
                floorRepository.saveAll(listOf(mlFloor1, mlFloor2))
                
                // Create rooms for ML floors
                val mlRoom1 = Room(
                    name = "Computer Lab 1",
                    number = "ML-101",
                    type = "Laboratory",
                    capacity = 50,
                    description = "Computer lab with 50 workstations",
                    floor = mlFloor1
                )
                
                val mlRoom2 = Room(
                    name = "Lecture Hall",
                    number = "ML-102",
                    type = "Classroom",
                    capacity = 120,
                    description = "Large lecture hall with projector",
                    floor = mlFloor1
                )
                
                val mlRoom3 = Room(
                    name = "Meeting Room",
                    number = "ML-201",
                    type = "Meeting",
                    capacity = 15,
                    description = "Small meeting room",
                    floor = mlFloor2
                )
                
                roomRepository.saveAll(listOf(mlRoom1, mlRoom2, mlRoom3))
                println("Floors and rooms data loaded.")
                
                // Create places
                val places = listOf(
                    // Mario Laserna places
                    Place(
                        name = "Computer Lab 1",
                        code = "ML-101",
                        category = "Laboratory",
                        distance = "1st Floor",
                        coordinates = "4.602442,-74.065952",
                        imageUrl = "https://example.com/images/lab1.jpg",
                        building = marioLaserna
                    ),
                    Place(
                        name = "Auditorium",
                        code = "ML-AUD",
                        category = "Classroom",
                        distance = "2nd Floor",
                        coordinates = "4.602442,-74.065952",
                        imageUrl = "https://example.com/images/ml_aud.jpg",
                        building = marioLaserna
                    ),
                    Place(
                        name = "Cafeteria",
                        code = "ML-CAF",
                        category = "Food",
                        distance = "Ground Floor",
                        coordinates = "4.602442,-74.065952",
                        imageUrl = "https://example.com/images/ml_cafe.jpg",
                        building = marioLaserna
                    ),
                    
                    // W Building places
                    Place(
                        name = "Chemistry Lab",
                        code = "W-201",
                        category = "Laboratory",
                        distance = "2nd Floor",
                        coordinates = "4.603018,-74.066217",
                        imageUrl = "https://example.com/images/w_chem.jpg",
                        building = wBuilding
                    ),
                    Place(
                        name = "Physics Lab",
                        code = "W-301",
                        category = "Laboratory",
                        distance = "3rd Floor",
                        coordinates = "4.603018,-74.066217",
                        imageUrl = "https://example.com/images/w_phys.jpg",
                        building = wBuilding
                    ),
                    
                    // Santo Domingo places
                    Place(
                        name = "Main Reading Room",
                        code = "SD-1",
                        category = "Study Area",
                        distance = "1st Floor",
                        coordinates = "4.602780,-74.065553",
                        imageUrl = "https://example.com/images/sd_reading.jpg",
                        building = santoDomingo
                    ),
                    Place(
                        name = "Computer Zone",
                        code = "SD-2",
                        category = "Study Area",
                        distance = "2nd Floor",
                        coordinates = "4.602780,-74.065553",
                        imageUrl = "https://example.com/images/sd_computers.jpg",
                        building = santoDomingo
                    ),
                    
                    // Sports Center places
                    Place(
                        name = "Swimming Pool",
                        code = "Z-POOL",
                        category = "Sports",
                        distance = "Ground Floor",
                        coordinates = "4.601982,-74.064964",
                        imageUrl = "https://example.com/images/z_pool.jpg",
                        building = sportsCenter
                    ),
                    Place(
                        name = "Gym",
                        code = "Z-GYM",
                        category = "Sports",
                        distance = "1st Floor",
                        coordinates = "4.601982,-74.064964",
                        imageUrl = "https://example.com/images/z_gym.jpg",
                        building = sportsCenter
                    )
                )
                
                placeRepository.saveAll(places)
                println("Places data loaded.")
                
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

                println("Sample data initialization complete!")
            } else {
                println("Database already contains data, skipping initialization.")
            }
        }
    }
}