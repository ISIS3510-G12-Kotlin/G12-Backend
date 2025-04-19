package com.example.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "places")
data class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var code: String? = null,

    @Column(nullable = true)
    var category: String? = null,

    @Column(nullable = true)
    var floor: String? = null,

    @Column(nullable = true)
    var coordinates: String? = null,

    @Column(name = "image_url", nullable = true)
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    var building: Building? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

// DTO for converting from Supabase format
data class PlaceDTO(
    val id: Long,
    val id_location: Long,
    val name: String,
    val url_image: String?,
    val floor: String?
)