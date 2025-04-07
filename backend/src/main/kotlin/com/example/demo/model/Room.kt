package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "rooms")
data class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var number: String,

    @Column(nullable = true)
    var type: String? = null,

    @Column(nullable = true)
    var capacity: Int? = null,

    @Column(nullable = true)
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    var floor: Floor
)
