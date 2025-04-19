package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "floors")
data class Floor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var number: Int,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = true)
    var description: String? = null,

    @Column(name = "floor_plan_url", nullable = true)
    var floorPlanUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    var building: Building,  // 🔄 modifiable in services

    @OneToMany(mappedBy = "floor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var rooms: MutableList<Room> = mutableListOf()
)
