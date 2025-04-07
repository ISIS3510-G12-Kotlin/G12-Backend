package com.example.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "buildings")
data class Building(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var code: String,

    @Column(nullable = true)
    var description: String? = null,

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double,

    @Column(nullable = true)
    var imageUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "building", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var floors: MutableList<Floor> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    var campus: Campus? = null,

    @OneToMany(mappedBy = "building", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userFavorites: MutableList<UserFavoriteBuilding> = mutableListOf()
)
