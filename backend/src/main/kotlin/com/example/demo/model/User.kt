package com.example.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, unique = true)
    val username: String,
    
    @Column(nullable = false)
    val email: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Column(name = "first_name", nullable = true)
    val firstName: String? = null,
    
    @Column(name = "last_name", nullable = true)
    val lastName: String? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val favoriteBuildings: MutableList<UserFavoriteBuilding> = mutableListOf()
)