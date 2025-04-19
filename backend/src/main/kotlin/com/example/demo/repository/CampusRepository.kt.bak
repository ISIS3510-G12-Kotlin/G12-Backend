package com.example.demo.repository

import com.example.demo.model.Campus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CampusRepository : JpaRepository<Campus, Long> {
    fun findByName(name: String): Optional<Campus>
}