package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "campus")
data class Campus(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var description: String? = null,

    @OneToMany(mappedBy = "campus", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var buildings: MutableList<Building> = mutableListOf()
)
